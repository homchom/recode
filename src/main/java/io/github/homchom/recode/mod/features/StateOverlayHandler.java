package io.github.homchom.recode.mod.features;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.DFInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.*;
import net.minecraft.util.FormattedCharSequence;

public class StateOverlayHandler {

    private static LegacyState state;

    public static void setState(LegacyState state) {
        StateOverlayHandler.state = state;
    }

    public static void drawStateOverlay(Font tr, PoseStack stack) {
        if (DFInfo.isOnDF()) {
            if (state != null) {
                if (state.getPlot() != null) {
                    drawTextRight(Component.literal(state.getPlot().getName() + " by " + state.getPlot().getOwner()).withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.GOLD))).getVisualOrderText(), 2, tr, stack);
                    drawTextRight(Component.literal("on Node " + state.getNode().getIdentifier()).withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.YELLOW))).getVisualOrderText(), 12, tr, stack);
                    drawTextRight(Component.literal("/join " + state.plot.getId()).withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY))).getVisualOrderText(), 22, tr, stack);
                    drawTextRight(Component.literal("You are currently " + state.mode.getContinuousVerb()).withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_AQUA))).getVisualOrderText(), 32, tr, stack);
                    if (state.isInSession()) drawTextRight(Component.literal("In a support session").withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE))).getVisualOrderText(), 42, tr, stack);
                }else {
                    if (state.getNode() != null) drawTextRight(Component.literal("At Node " + state.getNode().getIdentifier() + " Spawn").withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.YELLOW))).getVisualOrderText(), 2, tr, stack);
                }
            }
        }
    }

    private static void drawTextRight(FormattedCharSequence text, int y, Font tr, PoseStack stack) {
        int x = LegacyRecode.MC.getWindow().getGuiScaledWidth() - tr.width(text) - 4;
        tr.drawShadow(stack, text, x, y, 0xffffff);
    }

}
