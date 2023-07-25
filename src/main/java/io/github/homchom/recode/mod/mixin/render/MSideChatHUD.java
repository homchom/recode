package io.github.homchom.recode.mod.mixin.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.render.SideChatComponent;
import io.github.homchom.recode.sys.sidedchat.ChatRule;
import io.github.homchom.recode.sys.util.OrderedTextUtil;
import io.github.homchom.recode.sys.util.SoundUtil;
import io.github.homchom.recode.sys.util.TextUtil;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

// TODO: refactor further to remove remaining duplicate code

@Mixin(ChatComponent.class)
public abstract class MSideChatHUD implements SideChatComponent {
    private final List<GuiMessage.Line> sideVisibleMessages = Lists.newArrayList();

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private int chatScrollbarPos;

    @Shadow
    @Final
    private List<GuiMessage.Line> trimmedMessages;
    private int sideScrolledLines;

    private boolean renderingSideChat = false;

    private List<GuiMessage.Line> messagesCopy;

    @Shadow
    protected abstract boolean isChatFocused();

    @Shadow
    protected abstract boolean isChatHidden();

    @Shadow
    public abstract double getScale();

    @Shadow
    public abstract int getLinesPerPage();

    @Shadow
    public abstract int getWidth();

    @Shadow public abstract void render(GuiGraphics guiGraphics, int i, int j, int k);

    @Override
    public void renderSide(@NotNull GuiGraphics guiGraphics, int tickDelta, int mouseX, int mouseY) {
        renderingSideChat = true;
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(getSideChatStartX(), 0, 0);
        try {
            render(guiGraphics, tickDelta, mouseX, mouseY);
        } finally {
            poseStack.popPose();
            renderingSideChat = false;
        }
    }

    // TODO: improve render mixin functions further

    @Inject(method = "render", at = @At("HEAD"))
    private void copyForStacking(GuiGraphics guiGraphics, int tickDelta, int mouseX, int mouseY, CallbackInfo ci) {
        if (Config.getBoolean("stackDuplicateMsgs")) {
            messagesCopy = new ArrayList<>();
            messagesCopy.addAll(messagesToRender());
            messagesToRender().clear();
            messagesToRender().addAll(stackMsgs(messagesCopy));
        }
    }

    @Redirect(method = "render", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;trimmedMessages:Ljava/util/List;",
            opcode = Opcodes.GETFIELD
    ))
    private List<GuiMessage.Line> redirectRenderMessages(ChatComponent instance) {
        return messagesToRender();
    }

    // TODO: improve handling of chat queue (partition it?)
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/chat/ChatListener;queueSize()J"
    ))
    private long redirectRenderQueueSize(ChatListener instance) {
        return renderingSideChat ? 0 : instance.queueSize();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void restoreFromStacking(GuiGraphics guiGraphics, int tickDelta, int mouseX, int mouseY, CallbackInfo ci) {
        if (Config.getBoolean("stackDuplicateMsgs")) {
            messagesToRender().clear();
            messagesToRender().addAll(messagesCopy);
            messagesCopy = null;
        }
    }

    private List<GuiMessage.Line> messagesToRender() {
        return renderingSideChat ? sideVisibleMessages : trimmedMessages;
    }

    private int getSideChatStartX() {
        return this.minecraft.getWindow().getGuiScaledWidth() - getSideChatWidth() - 2;
    }

    private int getSideChatWidth() {
        int configWidth = Config.getInteger("sidechat_width");

        // if the width in config is valid
        if (configWidth > 0) {
            return configWidth;
        } else { // else if 0 or less, auto size the side chat
            int rawWidth = Math.min(
                (this.minecraft.getWindow().getGuiScaledWidth() - getWidth() - 14),
                getWidth()
            );
            // if the calculated width <= 0 (window really small), have 1 as a failsafe value
            return rawWidth > 0 ? rawWidth : 1;
        }
    }

    // just incase i want to re-add the option to change side chat scale
    private double getSideChatScale() {
        return getScale();
    }

    @Inject(method = "clearMessages", at = @At("TAIL"))
    private void clearMessages(boolean clearHistory, CallbackInfo ci) {
        sideVisibleMessages.clear();
    }


    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V", at = @At("TAIL"))
    private void addMessage(Component component, MessageSignature messageSignature, int messageId, GuiMessageTag guiMessageTag, boolean refresh, CallbackInfo ci) {
        boolean matchedARule = false;
        for (ChatRule chatRule : ChatRule.getChatRules()) {
            // compare against all rules
            if (chatRule.matches(component)) {
                // also don't add to chat if the chat side is either
                if (!matchedARule && chatRule.getChatSide() != ChatRule.ChatSide.EITHER) {
                    addToChat(chatRule.getChatSide(), component, messageId, guiMessageTag);
                    matchedARule = true;
                }

                // dont play sound if message is just being refreshed (ie, when window changes size)
                // & dont try to play a null sound (when the sound is set to 'None')
                if (!refresh && chatRule.getChatSound() != null) {
                    SoundUtil.playSound(chatRule.getChatSound());
                }
            }
        }

        // if matched rule, remove last from
        if (matchedARule) {
            int i = Mth.floor((double) this.getWidth() / this.getScale());
            int addedMessageCount = ComponentRenderUtils.wrapComponents(component, i,
                this.minecraft.font).size();
            // remove the last addedMessageCount messages from the visible messages
            // this has the effect of removing last message sent to main (to go to side instead)
            trimmedMessages.subList(0, addedMessageCount).clear();
        }
    }

    private void addToChat(ChatRule.ChatSide side, Component message, int chatLineId,
        GuiMessageTag guiMessageTag) {
        int i;
        switch (side) {
            case MAIN:
            default:
                i = Mth.floor((double) this.getWidth() / this.getScale());
                break;
            case SIDE:
                i = Mth.floor((double) this.getSideChatWidth() / this.getSideChatScale());
                break;
        }

        List<FormattedCharSequence> list = ComponentRenderUtils.wrapComponents(message, i, this.minecraft.font);
        for(int k = 0; k < list.size(); ++k) {
            FormattedCharSequence formattedCharSequence = list.get(k);

            boolean bl3 = k == list.size() - 1;
            this.getChatLines(side).add(0, new GuiMessage.Line(chatLineId, formattedCharSequence, guiMessageTag, bl3));

        }
    }

    private List<GuiMessage.Line> getChatLines(ChatRule.ChatSide chatSide) {
        switch (chatSide) {
            case MAIN:
            default:
                return trimmedMessages;
            case SIDE:
                return sideVisibleMessages;
        }
    }

    @Inject(method = "rescaleChat", at = @At("HEAD"))
    private void rescaleChat(CallbackInfo ci) {
        sideVisibleMessages.clear();
    }

    // another copy from minecraft decompiled code
    // the main difference is switching references from main to side chat
    // and subtracting getSideChatStartX() from the adjusted x
    @Inject(method = "getClickedComponentStyleAt", at = @At("HEAD"), cancellable = true)
    private void getClickedComponentStyleAt(double x, double y, CallbackInfoReturnable<Style> cir) {
        if (this.isChatFocused() && !this.minecraft.options.hideGui && !this.isChatHidden()) {
            double scale = this.getSideChatScale();
            double adjustedX = (x - 2.0D) - getSideChatStartX();
            double adjustedY = (double) this.minecraft.getWindow().getGuiScaledHeight() - y - 40.0D;
            adjustedX = Mth.floor(adjustedX / scale);
            adjustedY = Mth.floor(
                adjustedY / (scale * (this.minecraft.options.chatLineSpacing().get() + 1.0D)));
            if (!(adjustedX < 0.0D) && !(adjustedY < 0.0D)) {
                int size = Math.min(this.getLinesPerPage(), this.sideVisibleMessages.size());
                if (adjustedX <= (double) Mth.floor(
                    (double) this.getSideChatWidth() / scale)) {
                    if (adjustedY < (double) (9 * size + size)) {
                        int line = (int) (adjustedY / 9.0D + (double) sideScrolledLines);
                        if (line >= 0 && line < this.sideVisibleMessages.size()) {
                            GuiMessage.Line chatHudLine = this.sideVisibleMessages.get(line);
                            cir.setReturnValue(this.minecraft.font.getSplitter()
                                .componentStyleAtWidth(chatHudLine.content(), (int) adjustedX));
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "resetChatScroll", at = @At("TAIL"))
    private void resetChatScroll(CallbackInfo ci) {
        sideScrolledLines = 0;
    }

    @Inject(method = "scrollChat", at = @At("TAIL"))
    private void scrollChat(int amount, CallbackInfo ci) {
        sideScrolledLines = (int) ((double) this.chatScrollbarPos + amount);
        int i = this.sideVisibleMessages.size();
        if (sideScrolledLines > i - this.getLinesPerPage()) {
            sideScrolledLines = i - this.getLinesPerPage();
        }

        if (sideScrolledLines <= 0) {
            sideScrolledLines = 0;
        }
    }

    // Message Stacker
    public List<GuiMessage.Line> stackMsgs(List<GuiMessage.Line> msgs) {
        GuiMessage.Line last = null;
        int count = 1;

        List<GuiMessage.Line> copy = new ArrayList<>();

        for (GuiMessage.Line msg : msgs) {
            if (last == null) {
                last = msg;
            } else {
                if (OrderedTextUtil.getString(last.content())
                    .equals(OrderedTextUtil.getString(msg.content()))) {
                    count++;
                } else {
                    if (count == 1) {
                        copy.add(last);
                    } else {
                        copy.add(new GuiMessage.Line(
                                last.addedTime(),
                                FormattedCharSequence.composite(
                                    last.content(),
                                    TextUtil.colorCodesToTextComponent(" §bx" + count).getVisualOrderText()),
                                last.tag(),
                                true
                            )
                        );
                    }
                    count = 1;
                    last = msg;
                }
            }
        }
        if (last != null) {
            copy.add(last);
        }
        return copy;
    }
}
