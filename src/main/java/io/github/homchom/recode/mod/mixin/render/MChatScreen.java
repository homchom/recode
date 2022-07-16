package io.github.homchom.recode.mod.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.VarSyntaxHighlighter;
import io.github.homchom.recode.sys.sidedchat.ChatShortcut;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class MChatScreen {
    @Shadow
    protected EditBox input;

    @Shadow
    @Final
    private String initial;

    @Inject(method = "render", at = @At("TAIL"))
    private void render(PoseStack poseStack, int mouseX, int mouseY, float delta,
        CallbackInfo ci) {
        if (Config.getBoolean("highlightVarSyntax")) {
            Minecraft mc = LegacyRecode.MC;

            String text = input.getValue();

            if (text.startsWith("/") && !(
                text.startsWith("/var") ||
                    text.startsWith("/variable") ||
                    text.startsWith("/num") ||
                    text.startsWith("/number") ||
                    text.startsWith("/txt") ||
                    text.startsWith("/text")
            )) {
                boolean r = true;
                for (String o : VarSyntaxHighlighter.txtPreviews) {
                    if (o.endsWith(" N")) o = o.replace(" N","");
                    if (text.startsWith(o)) {
                        r = false;
                        break;
                    }
                }

                if (r) return;
            }

            Component formatted = VarSyntaxHighlighter.highlight(text);

            if (formatted != null) {
                mc.font.drawShadow(poseStack, formatted, 4, mc.screen.height - 25,
                    0xffffff);
            }
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ChatScreen;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"), index = 5)
    private int getTextboxColor(int defaultColour) {
        ChatShortcut currentChatShortcut = ChatShortcut.getCurrentChatShortcut();

        // if there is one active - use it
        if (currentChatShortcut != null) {
            return currentChatShortcut.getColor().getRGB();
        }
        // else use the default minecraft option
        else return defaultColour;
    }

    @ModifyArg(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ChatScreen;handleChatInput(Ljava/lang/String;Z)V"), index = 0)
    private String insertPrefix(String interceptedMessage) {
        ChatShortcut currentChatShortcut = ChatShortcut.getCurrentChatShortcut();

        if (currentChatShortcut != null) {
            // the prefix already includes the space
            return currentChatShortcut.getPrefix() + interceptedMessage;
        }
        // else just send the message
        else return interceptedMessage;
    }
}
