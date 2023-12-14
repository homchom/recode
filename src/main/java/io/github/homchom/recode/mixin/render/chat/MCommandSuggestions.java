package io.github.homchom.recode.mixin.render.chat;

import io.github.homchom.recode.feature.visual.ExpressionHighlighter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(CommandSuggestions.class)
public abstract class MCommandSuggestions {
    @Unique
    private final ExpressionHighlighter highlighter = new ExpressionHighlighter();

    @Unique
    private @Nullable FormattedCharSequence preview = null;

    @Inject(method = "formatChat", at = @At("RETURN"), cancellable = true)
    private void highlightAndPreview(
            String partialInput,
            int position,
            CallbackInfoReturnable<FormattedCharSequence> cir
    ) {
        var formatted = cir.getReturnValue();
        var player = Objects.requireNonNull(Minecraft.getInstance().player);
        var highlighted = highlighter.runHighlighting(partialInput, formatted, player);
        if (highlighted == null) return;

        if (highlighted.getPreview() != null) preview = highlighted.getPreview();
        cir.setReturnValue(highlighted.getText());
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void renderPreview(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
        if (preview != null) {
            var font = Minecraft.getInstance().font;
            var y = Objects.requireNonNull(Minecraft.getInstance().screen).height - 25;
            guiGraphics.drawString(font, preview, 4, y, 0xffffff, true);
        }
        preview = null;
    }
}