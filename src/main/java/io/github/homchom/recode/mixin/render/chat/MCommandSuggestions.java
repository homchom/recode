package io.github.homchom.recode.mixin.render.chat;

import io.github.homchom.recode.feature.visual.EditBoxExpressionFormatter;
import io.github.homchom.recode.hypercube.DFValueMeta;
import io.github.homchom.recode.hypercube.DFValues;
import io.github.homchom.recode.mod.config.Config;
import kotlin.ranges.IntRange;
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
    private final EditBoxExpressionFormatter highlighter =
            new EditBoxExpressionFormatter(true, () -> {
                var player = Objects.requireNonNull(Minecraft.getInstance().player);
                var meta = DFValues.dfValueMeta(player.getMainHandItem());
                if (meta instanceof DFValueMeta.Primitive || meta instanceof DFValueMeta.Variable) {
                    return meta.getType().equals("comp");
                }
                return null;
            });

    @Unique
    private @Nullable FormattedCharSequence preview = null;

    @Shadow @Final EditBox input;

    // expression highlighting and preview
    @Inject(method = "formatChat", at = @At("RETURN"), cancellable = true)
    private void highlightAndPreview(
            String partialInput,
            int position,
            CallbackInfoReturnable<FormattedCharSequence> cir
    ) {
        if (!Config.getBoolean("highlightVarSyntax")) return;
        var formatted = highlighter.format(
                input.getValue(),
                cir.getReturnValue(),
                new IntRange(position, position + partialInput.length() - 1)
        );
        if (formatted != null) {
            cir.setReturnValue(formatted.getText());
            preview = formatted.getPreview();
        }
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