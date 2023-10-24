package io.github.homchom.recode.mixin.render.chat;

import io.github.homchom.recode.feature.visual.ExpressionHighlighter;
import io.github.homchom.recode.util.Computation;
import io.github.homchom.recode.util.mixin.MixinCustomField;
import kotlin.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(CommandSuggestions.class)
public class MCommandSuggestions {
    // (input, output)
    @Unique
    private final MixinCustomField<@Nullable Pair<String, FormattedCharSequence>, CommandSuggestions> highlight =
        new MixinCustomField<>(() -> null);

    @Inject(method = "formatChat", at = @At("HEAD"), cancellable = true)
    private void formatExpressionHighlighting(
            String input,
            int displayPos,
            CallbackInfoReturnable<FormattedCharSequence> cir
    ) {
        var currentHighlight = highlight.get(thisCommandSuggestions());

        if (currentHighlight != null && currentHighlight.getFirst().equals(input)) {
            cir.setReturnValue(currentHighlight.getSecond());
            return;
        }

        var mainHandItem = Objects.requireNonNull(Minecraft.getInstance().player).getMainHandItem();
        var comp = ExpressionHighlighter.INSTANCE.runHighlighting(input, mainHandItem);
        if (comp instanceof Computation.Success<?> success) {
            var formatted = Language.getInstance().getVisualOrder((Component) success.getValue());
            highlight.set(thisCommandSuggestions(), new Pair<>(input, formatted));
            cir.setReturnValue(formatted);
        }
    }

    @Unique
    private CommandSuggestions thisCommandSuggestions() {
        return (CommandSuggestions) (Object) this;
    }
}