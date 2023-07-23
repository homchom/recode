package io.github.homchom.recode.mod.features;

import io.github.homchom.recode.multiplayer.state.DF;
import io.github.homchom.recode.multiplayer.state.DFState;
import io.github.homchom.recode.multiplayer.state.PlayState;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;

public class StateOverlayHandler {
    private static DFState state;

    public static void setState(DFState state) {
        StateOverlayHandler.state = state;
    }

    public static void drawStateOverlay(Font tr, GuiGraphics guiGraphics) {
        if (DF.isOnDF()) {
            if (state != null) {
                if (state instanceof PlayState playState) {
                    drawTextRight(Component.literal(playState.getPlot().getName() + " by " + playState.getPlot().getOwner()).withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.GOLD))).getVisualOrderText(), 2, tr, guiGraphics);
                    drawTextRight(Component.literal("on " + DF.getNodeDisplayName(state)).withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.YELLOW))).getVisualOrderText(), 12, tr, guiGraphics);
                    drawTextRight(Component.literal("/join " + playState.getPlot().getId()).withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY))).getVisualOrderText(), 22, tr, guiGraphics);
                    drawTextRight(Component.literal("You are currently " + playState.getMode().getCapitalizedDescriptor()).withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_AQUA))).getVisualOrderText(), 32, tr, guiGraphics);
                    if (state.getSession() != null) drawTextRight(Component.literal("In a support session").withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE))).getVisualOrderText(), 42, tr, guiGraphics);
                } else {
                    drawTextRight(Component.literal("At " + DF.getNodeDisplayName(state) + " Spawn").withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.YELLOW))).getVisualOrderText(), 2, tr, guiGraphics);
                }
            }
        }
    }

    private static void drawTextRight(FormattedCharSequence text, int y, Font tr, GuiGraphics guiGraphics) {
        int x = Minecraft.getInstance().getWindow().getGuiScaledWidth() - tr.width(text) - 4;
        guiGraphics.drawString(tr, text, x, y, 0xffffff);
    }

}
