package io.github.codeutilities.mod.mixin.render;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.mod.features.VarSyntaxHighlighter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class MChatScreen {

    @Shadow
    protected TextFieldWidget chatField;

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
                return;
            }

            Text formatted = VarSyntaxHighlighter.highlight(text);

            if (formatted != null) {
                mc.textRenderer.drawWithShadow(matrices, formatted, 4, mc.currentScreen.height - 25,
                    0xffffff);
            }
        }
    }

}
