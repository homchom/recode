package io.github.homchom.recode.mixin.render.chat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.homchom.recode.config.Config;
import io.github.homchom.recode.feature.social.DGuiWithSideChat;
import io.github.homchom.recode.feature.social.SideChat;
import io.github.homchom.recode.feature.visual.FMessageStacking;
import io.github.homchom.recode.render.text.FormattedCharSequenceExtensions;
import io.github.homchom.recode.ui.MessageTags;
import io.github.homchom.recode.ui.RecodeMessageTags;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;

@Mixin(ChatComponent.class)
public abstract class MChatComponent {
    @Shadow @Final private List<GuiMessage.Line> trimmedMessages;

    // side chat "overrides" for private members

    @ModifyVariable(method = "screenToChatX", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double overrideScreenToChatX(double screenX) {
        var sideChat = asSideChatOrNull();
        if (sideChat != null) screenX -= sideChat.getXOffset();
        return screenX;
    }

    // side chat redirects

    // TODO: improve handling of chat queue (partition it?)
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/chat/ChatListener;queueSize()J"
    ))
    private long wrapMessageQueueSize(long size) {
        return isSideChat() ? 0 : size;
    }

    // actions synced between main chat and side chat

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickSideChat(CallbackInfo ci) {
        if (isMainChat()) getSideChat().tick();
    }

    @Inject(method = "scrollChat", at = @At("HEAD"))
    private void scrollSideChat(int amount, CallbackInfo ci) {
        if (isMainChat()) getSideChat().scrollChat(amount);
    }

    @Inject(method = "clearMessages", at = @At("HEAD"))
    private void clearSideChat(boolean refresh, CallbackInfo ci) {
        if (isMainChat()) getSideChat().clearMessages(refresh);
    }

    // side chat helper methods

    @Unique
    private SideChat getSideChat() {
        var gui = (DGuiWithSideChat) Minecraft.getInstance().gui;
        return gui.getRecode$sideChat();
    }

    @Unique
    private boolean isMainChat() {
        var instance = (ChatComponent) (Object) this;
        return instance == Minecraft.getInstance().gui.getChat();
    }

    @Unique
    private boolean isSideChat() {
        var instance = (ChatComponent) (Object) this;
        return instance instanceof SideChat;
    }

    @Unique
    private @Nullable SideChat asSideChatOrNull() {
        var instance = (ChatComponent) (Object) this;
        return instance instanceof SideChat sideChat ? sideChat : null;
    }

    // message stacking

    @Unique
    private int messageStackAmount = 1;

    @ModifyExpressionValue(
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ComponentRenderUtils;wrapComponents(Lnet/minecraft/network/chat/FormattedText;ILnet/minecraft/client/gui/Font;)Ljava/util/List;"
            )
    )
    private List<FormattedCharSequence> stackMessages(
            List<FormattedCharSequence> lineMessages,
            Component fullMessage,
            MessageSignature signature,
            int tickDelta,
            GuiMessageTag tag,
            boolean refresh,
            @Share("cancel") LocalBooleanRef cancel
    ) {
        cancel.set(false);
        if (Boolean.FALSE.equals(Config.get(FMessageStacking.INSTANCE))) {
            return lineMessages;
        }

        // trimmedMessages[0] is the most recent message
        var lineCount = lineMessages.size();
        if (trimmedMessages.size() < lineCount) return lineMessages;
        if (trimmedMessages.size() > lineCount) {
            if (!trimmedMessages.get(lineCount).endOfEntry()) return lineMessages;
        }

        // reset and return if messages aren't equal
        for (var index = 0; index < lineCount; index++) {
            var line1 = trimmedMessages.get(index);
            var line2 = lineMessages.get(lineCount - 1 - index);
            var endsEarly = index != 0 && line1.endOfEntry();
            if (!FormattedCharSequenceExtensions.looksLike(line1.content(), line2) || endsEarly) {
                messageStackAmount = 1;
                return lineMessages;
            }
        }

        // replace old message, with updated tags
        var newTag = MessageTags.plus(tag, RecodeMessageTags.INSTANCE.stacked(++messageStackAmount));
        for (var index = 0; index < lineCount; index++) {
            var oldLine = trimmedMessages.get(index);
            var newLine = new GuiMessage.Line(
                    oldLine.addedTime(),
                    oldLine.content(),
                    newTag,
                    oldLine.endOfEntry()
            );
            trimmedMessages.set(index, newLine);
        }

        // cancel in the next handler and return a dummy value
        cancel.set(true);
        return Collections.emptyList();
    }

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ComponentRenderUtils;wrapComponents(Lnet/minecraft/network/chat/FormattedText;ILnet/minecraft/client/gui/Font;)Ljava/util/List;",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void cancelAfterMessageStacking(CallbackInfo ci, @Share("cancel") LocalBooleanRef cancel) {
        if (cancel.get()) ci.cancel();
    }
}
