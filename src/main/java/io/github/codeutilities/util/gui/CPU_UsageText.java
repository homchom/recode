package io.github.codeutilities.util.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.Level;

public class CPU_UsageText {

    private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private static final Window mainWindow = MinecraftClient.getInstance().getWindow();
    public static boolean hasLagSlayer;
    public static boolean lagSlayerEnabled;
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

        System.out.println(barsText);

        int sibs = barsText.getSiblings().size();

        Text pText = barsText.getSiblings().get(sibs - 2);
        pText.getSiblings().add(barsText.getSiblings().get(sibs - 1));

        barsText.getSiblings().remove(sibs - 1);
        barsText.getSiblings().remove(sibs - 2);
        barsText.getSiblings().remove(0);

        String numberStr = pText.asString().replaceAll("\\(", "").replaceAll("\\)", "");
        String numberColor = msgPart.get("color").getAsString();

        if (numberColor.equals("dark_gray")) numberColor = "white";

        numberText = Text.Serializer.fromJson("{\"extra\":[{\"italic\":false,\"color\":\"white\",\"text\":\"(\"}," +
                "{\"italic\":false,\"color\":\"" + numberColor + "\",\"text\":\"" + numberStr + "%\"}," +
                "{\"italic\":false,\"color\":\"white\",\"text\":\")\"}],\"text\":\"\"}");

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

        try {
            renderText(stack, Formatting.GOLD.getColorValue());
            renderText(stack, barsText, 2);
            renderText(stack, numberText, 1);
        } catch (Exception e) {
            CodeUtilities.log(Level.ERROR, "Error while trying to render LagSlayer HUD!");
            e.printStackTrace();
        }
    }

    private static void renderText(MatrixStack stack, Text text, int line) {
        textRenderer.draw(stack, text, 5, mainWindow.getScaledHeight() - (textRenderer.fontHeight * line), 0xffffff);
    }

    private static void renderText(MatrixStack stack, int color) {
        textRenderer.draw(stack, "CPU Usage:", 5, mainWindow.getScaledHeight() - (textRenderer.fontHeight * 3), color);
    }
}
