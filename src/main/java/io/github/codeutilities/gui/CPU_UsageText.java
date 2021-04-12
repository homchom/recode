package io.github.codeutilities.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CPU_UsageText {

    private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private static final Window mainWindow = MinecraftClient.getInstance().getWindow();
    public static boolean hasLagSlayer;
    public static boolean lagSlayerEnabled;
    public static String monitorPlotId;
    private static Text barsText;
    private static Text numberText;
    private static long lastUpdate;

    public CPU_UsageText() {
        throw new RuntimeException("CPU_UsageText is a static class !");
    }

    public static void updateCPU(TitleS2CPacket packet) {
        JsonArray msgArray = Text.Serializer.toJsonTree(packet.getText()).getAsJsonObject().getAsJsonArray("extra");
        JsonObject msgPart = msgArray.get(2).getAsJsonObject();

        barsText = packet.getText();
        barsText.getSiblings().remove(0);

        String numberStr = "";
        String numberColor = msgPart.get("color").getAsString();
        if (!numberColor.equals("dark_gray")) {
            int coloredBoxes = msgPart.get("text").getAsString().length();

            if (coloredBoxes == 20) {
                numberStr = "100%";
            } else {
                int cpuFrom = (coloredBoxes * 5);
                int cpuTo = cpuFrom + 5;

                numberStr = cpuFrom + "% - " + cpuTo + "%";
            }
        } else {
            numberStr = "0% - 5%";
            //numberColor = "#666666";

            //MutableText barsTextMiddle = (MutableText)barsText.getSiblings().get(1);
            //barsTextMiddle.setStyle(barsTextMiddle.getStyle().withColor(Formatting.);
        }

        numberText = Text.Serializer.fromJson("{\"extra\":[{\"italic\":false,\"color\":\"gray\",\"text\":\"[\"}," +
                "{\"italic\":false,\"color\":\"" + numberColor + "\",\"text\":\"" + numberStr + "\"}," +
                "{\"italic\":false,\"color\":\"gray\",\"text\":\"]\"}],\"text\":\"\"}");

        lastUpdate = System.currentTimeMillis();
    }

    public static void onRender(MatrixStack stack) {

        if (barsText == null || numberText == null) return;
        if ((System.currentTimeMillis() - lastUpdate) > 1200) {
            barsText = null;
            numberText = null;
            hasLagSlayer = false;
            return;
        }

        hasLagSlayer = true;
        renderText(stack, "CPU Usage:", 3, Formatting.GOLD.getColorValue());
        renderText(stack, barsText, 2);
        renderText(stack, numberText, 1);
    }

    private static void renderText(MatrixStack stack, Text text, int line) {
        textRenderer.draw(stack, text, 5, mainWindow.getScaledHeight() - (textRenderer.fontHeight * line), 0xffffff);
    }

    private static void renderText(MatrixStack stack, String text, int line, int color) {
        textRenderer.draw(stack, text, 5, mainWindow.getScaledHeight() - (textRenderer.fontHeight * line), color);
    }
}
