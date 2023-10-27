package io.github.homchom.recode.mixin.render.chat;

import io.github.homchom.recode.feature.visual.ExpressionHighlighter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.locale.Language;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(CommandSuggestions.class)
public abstract class MCommandSuggestions {
    @Shadow @Final EditBox input;

    @Unique
    private final ExpressionHighlighter highlighter = new ExpressionHighlighter();

    @Inject(method = "formatChat", at = @At("HEAD"), cancellable = true)
    private void runHighlighting(
            String partialInput,
            int position,
            CallbackInfoReturnable<FormattedCharSequence> cir
    ) {
        var player = Objects.requireNonNull(Minecraft.getInstance().player);

        var highlighted = highlighter.runHighlighting(partialInput, player);
        if (highlighted == null) return;
        var success = highlighted.asSuccess();
        if (success == null) return;

        var formatted = Language.getInstance().getVisualOrder(success.getValue());
        cir.setReturnValue(formatted);
    }
}