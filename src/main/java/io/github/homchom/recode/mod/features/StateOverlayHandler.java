package io.github.homchom.recode.mod.features;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.multiplayer.state.DF;
import io.github.homchom.recode.multiplayer.state.DFState;
import io.github.homchom.recode.multiplayer.state.PlayState;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.util.BasicTypeExtensions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;

public class StateOverlayHandler {
    private static DFState state;

    public static void setState(DFState state) {
        StateOverlayHandler.state = state;
    }

    public static void drawStateOverlay(Font tr, PoseStack stack) {
        if (DFInfo.isOnDF()) {
            if (state != null) {
                if (state instanceof PlayState playState) {
                    drawTextRight(Component.literal(playState.getPlot().getName() + " by " + playState.getPlot().getOwner()).withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.GOLD))).getVisualOrderText(), 2, tr, stack);
                    drawTextRight(Component.literal("on " + DF.getNodeDisplayName(state)).withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.YELLOW))).getVisualOrderText(), 12, tr, stack);
                    drawTextRight(Component.literal("/join " + playState.getPlot().getId()).withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY))).getVisualOrderText(), 22, tr, stack);
                    drawTextRight(Component.literal("You are currently " + BasicTypeExtensions.capitalize(playState.getMode().getDescriptor())).withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_AQUA))).getVisualOrderText(), 32, tr, stack);
                    // TODO: readd
                    //if (state.isInSession()) drawTextRight(Component.literal("In a support session").withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE))).getVisualOrderText(), 42, tr, stack);
                } else {
                    drawTextRight(Component.literal("At " + DF.getNodeDisplayName(state) + " Spawn").withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.YELLOW))).getVisualOrderText(), 2, tr, stack);
                }
            }
        }
    }

    private static void drawTextRight(FormattedCharSequence text, int y, Font tr, PoseStack stack) {
        int x = LegacyRecode.MC.getWindow().getGuiScaledWidth() - tr.width(text) - 4;
        tr.drawShadow(stack, text, x, y, 0xffffff);
    }

}
