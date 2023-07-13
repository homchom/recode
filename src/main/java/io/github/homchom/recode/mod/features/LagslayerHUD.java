package io.github.homchom.recode.mod.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.LegacyRecode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;

public class LagslayerHUD {
    private static final Font font = Minecraft.getInstance().font;
    private static final Window mainWindow = Minecraft.getInstance().getWindow();
    private static Component barsComponent;
    private static Component numberComponent;
    private static long lastUpdate;

    public static void updateCPU(ClientboundSetActionBarTextPacket packet) {
        JsonArray msgArray = Component.Serializer.toJsonTree(packet.getText()).getAsJsonObject().getAsJsonArray("extra");
        JsonObject msgPart = msgArray.get(2).getAsJsonObject();

        barsComponent = packet.getText();

        int siblings = barsComponent.getSiblings().size();

        Component pComponent = barsComponent.getSiblings().get(siblings - 2);
        pComponent.getSiblings().add(barsComponent.getSiblings().get(siblings - 1));

        barsComponent.getSiblings().remove(siblings - 1);
        barsComponent.getSiblings().remove(siblings - 2);
        barsComponent.getSiblings().remove(0);

        String numberStr = pComponent.getString().replaceAll("\\(", "").replaceAll("\\)", "");
        String numberColor = msgPart.get("color").getAsString();

        if (numberColor.equals("dark_gray")) numberColor = "white";

        numberComponent = Component.Serializer.fromJson("{\"extra\":[{\"italic\":false,\"color\":\"white\",\"text\":\"(\"}," +
                "{\"italic\":false,\"color\":\"" + numberColor + "\",\"text\":\"" + numberStr + "\"}," +
                "{\"italic\":false,\"color\":\"white\",\"text\":\")\"}],\"text\":\"\"}");

        lastUpdate = System.currentTimeMillis();
    }

    public static void onRender(GuiGraphics guiGraphics) {
        if (barsComponent == null || numberComponent == null) return;
        if ((System.currentTimeMillis() - lastUpdate) > 1200) {
            barsComponent = null;
            numberComponent = null;
            return;
        }

        try {
            renderComponent(guiGraphics, Component.literal("CPU Usage:"), 3, ChatFormatting.GOLD.getColor());
            renderComponent(guiGraphics, barsComponent, 2);
            renderComponent(guiGraphics, numberComponent, 1);
        } catch (Exception e) {
            LegacyRecode.error("Error while trying to render LagSlayer HUD");
            e.printStackTrace();
        }
    }

    private static void renderComponent(GuiGraphics guiGraphics, Component text, int line) {
        renderComponent(guiGraphics, text, line, 0xffff);
    }

    private static void renderComponent(GuiGraphics guiGraphics, Component text, int line, int color) {
        guiGraphics.drawString(font, text, 4, mainWindow.getGuiScaledHeight() - (font.lineHeight * line + 4), color, true);
    }
}
