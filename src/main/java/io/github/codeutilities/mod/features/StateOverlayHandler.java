package io.github.codeutilities.mod.features;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.sys.networking.State;
import io.github.codeutilities.sys.player.DFInfo;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class StateOverlayHandler {

    private static State state;

    public static void setState(State state) {
        StateOverlayHandler.state = state;
    }

    public static void drawStateOverlay(TextRenderer tr, MatrixStack stack) {
        if(DFInfo.isOnDF()) {
            if(state != null) {
                if(state.getPlot() != null) {
                    drawTextRight(new LiteralText(state.getPlot().getName() + " by " + state.getPlot().getOwner()).styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GOLD))).asOrderedText(), 2, tr, stack);
                    drawTextRight(new LiteralText("on Node " + state.getNode().getIdentifier()).styled(style -> style.withColor(TextColor.fromFormatting(Formatting.YELLOW))).asOrderedText(), 12, tr, stack);
                    drawTextRight(new LiteralText("/join " + state.plot.getId()).styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GRAY))).asOrderedText(), 22, tr, stack);

                }else {
                    drawTextRight(new LiteralText("At Node " + state.getNode().getIdentifier() + " Spawn").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.YELLOW))).asOrderedText(), 2, tr, stack);
                }
            }
        }
    }

    private static void drawTextRight(OrderedText text, int y, TextRenderer tr, MatrixStack stack) {
        int x = CodeUtilities.MC.getWindow().getScaledWidth() - tr.getWidth(text) - 4;
        tr.drawWithShadow(stack, text, x, y, 0xffffff);
    }

}
