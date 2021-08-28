package io.github.codeutilities.mod.mixin.render;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.mod.features.VarSyntaxHighlighter;
import io.github.codeutilities.sys.sidedchat.ChatShortcut;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class MChatScreen {

    @Shadow
    protected TextFieldWidget chatField;

    @Shadow private String originalChatText;

    @Inject(method = "render", at = @At("INVOKE"))
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta,
        CallbackInfo ci) {
        if (Config.getBoolean("highlightVarSyntax")) {
            MinecraftClient mc = CodeUtilities.MC;

            String text = chatField.getText();

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

            Text formatted = VarSyntaxHighlighter.highlight(text);

            if (formatted != null) {
                mc.textRenderer.drawWithShadow(matrices, formatted, 4, mc.currentScreen.height - 25,
                    0xffffff);
            }
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"), index = 5)
    private int getTextboxColour(int defaultColour) {
        ChatShortcut currentChatShortcut = ChatShortcut.getCurrentChatShortcut();

        // if there is one active - use it
        if (currentChatShortcut != null) {
            return currentChatShortcut.getColor().getRGB();
        }
        // else use the default minecraft option
        else return defaultColour;
    }

    @ModifyArg(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;sendMessage(Ljava/lang/String;)V"), index = 0)
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
