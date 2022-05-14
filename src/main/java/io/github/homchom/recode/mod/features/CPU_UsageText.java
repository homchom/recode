package io.github.homchom.recode.mod.features;

import com.google.gson.*;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.Recode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import org.apache.logging.log4j.Level;

public class CPU_UsageText {

    private static final Font textRenderer = Minecraft.getInstance().font;
    private static final Window mainWindow = Minecraft.getInstance().getWindow();
    public static boolean hasLagSlayer;
    public static boolean lagSlayerEnabled;
    private static Component barsText;
    private static Component numberText;
    private static long lastUpdate;

    public CPU_UsageText() {
        throw new RuntimeException("CPU_UsageText is a static class !");
    }

    public static void updateCPU(ClientboundSetTitleTextPacket packet) {
        JsonArray msgArray = Component.Serializer.toJsonTree(packet.getText()).getAsJsonObject().getAsJsonArray("extra");
        JsonObject msgPart = msgArray.get(2).getAsJsonObject();

        barsText = packet.getText();

        int sibs = barsText.getSiblings().size();

        Component pText = barsText.getSiblings().get(sibs - 2);
        pText.getSiblings().add(barsText.getSiblings().get(sibs - 1));

        barsText.getSiblings().remove(sibs - 1);
        barsText.getSiblings().remove(sibs - 2);
        barsText.getSiblings().remove(0);

        String numberStr = pText.getContents().replaceAll("\\(", "").replaceAll("\\)", "");
        String numberColor = msgPart.get("color").getAsString();

        if (numberColor.equals("dark_gray")) numberColor = "white";

        numberText = Component.Serializer.fromJson("{\"extra\":[{\"italic\":false,\"color\":\"white\",\"text\":\"(\"}," +
                "{\"italic\":false,\"color\":\"" + numberColor + "\",\"text\":\"" + numberStr + "%\"}," +
                "{\"italic\":false,\"color\":\"white\",\"text\":\")\"}],\"text\":\"\"}");

        lastUpdate = System.currentTimeMillis();
    }

    public static void onRender(PoseStack stack) {

        if (barsText == null || numberText == null) return;
        if ((System.currentTimeMillis() - lastUpdate) > 1200) {
            barsText = null;
            numberText = null;
            hasLagSlayer = false;
            return;
        }

        hasLagSlayer = true;

        try {
            renderText(stack, ChatFormatting.GOLD.getColor());
            renderText(stack, barsText, 2);
            renderText(stack, numberText, 1);
        } catch (Exception e) {
            Recode.error("Error while trying to render LagSlayer HUD!");
            e.printStackTrace();
        }
    }

    private static void renderText(PoseStack stack, Component text, int line) {
        textRenderer.draw(stack, text, 5, mainWindow.getGuiScaledHeight() - (textRenderer.lineHeight * line), 0xffffff);
    }

    private static void renderText(PoseStack stack, int color) {
        textRenderer.draw(stack, "CPU Usage:", 5, mainWindow.getGuiScaledHeight() - (textRenderer.lineHeight * 3), color);
    }
}
