package io.github.homchom.recode.mixin.render.chat;

import io.github.homchom.recode.feature.visual.ExpressionHighlighter;
import io.github.homchom.recode.ui.text.FormattedCharSequenceTransformations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow @Final EditBox input;

    @Inject(method = "formatChat", at = @At("RETURN"), cancellable = true)
    private void highlightAndPreview(
            String partialInput,
            int position,
            CallbackInfoReturnable<FormattedCharSequence> cir
    ) {
        var formatted = cir.getReturnValue();
        var player = Objects.requireNonNull(Minecraft.getInstance().player);
        var highlighted = highlighter.runHighlighting(input.getValue(), formatted, player);
        if (highlighted == null) return;

        if (highlighted.getPreview() != null) preview = highlighted.getPreview();

        FormattedCharSequence subSequence;
        if (position == 0 && partialInput.length() == input.getValue().length()) {
            subSequence = highlighted.getText();
        } else {
            subSequence = FormattedCharSequenceTransformations.subSequence(
                    highlighted.getText(),
                    position,
                    position + partialInput.length()
            );
        }

        cir.setReturnValue(subSequence);
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