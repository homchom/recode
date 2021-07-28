package io.github.codeutilities.mod.mixin.render;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.mod.features.VarSyntaxHighlighter;
import io.github.codeutilities.mod.features.commands.CodeSearcher;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
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

            Text formatted = VarSyntaxHighlighter.highlight(chatField.getText());

            if (formatted != null) {
                mc.textRenderer.drawWithShadow(matrices, formatted, 4, mc.currentScreen.height-25, 0xffffff);
            }
        }
    }

}
