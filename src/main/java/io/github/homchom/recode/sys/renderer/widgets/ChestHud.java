package io.github.homchom.recode.sys.renderer.widgets;

import com.google.common.collect.Lists;
import io.github.homchom.recode.hypercube.state.DF;
import io.github.homchom.recode.hypercube.state.PlotMode;
import io.github.homchom.recode.mod.config.LegacyConfig;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.opengl.GL11;

import java.util.List;

// TODO remove?
public class ChestHud {
    public static void register() {
        ScreenEvents.AFTER_INIT.register(ChestHud::afterInit);
    }

    private static void afterInit(Minecraft client, Screen screen, int windowWidth, int windowHeight) {
        if (screen instanceof ContainerScreen) {
            ScreenEvents.afterRender(screen).register(ChestHud::afterContainerRender);
        }
    }


    private static void afterContainerRender(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        if (DF.isInMode(DF.getCurrentDFState(), PlotMode.Dev.ID) && LegacyConfig.getBoolean("chestToolTip")) {
            if (LegacyConfig.getBoolean("chestToolTipType")) {
                ItemStack item = Minecraft.getInstance().player.getInventory().getItem(17);

                int i = ((screen.width) / 2) + 85;
                int j = (screen.height) / 2 - 68;

                // check if block in dev area later.
                if (Minecraft.getInstance().getWindow().getGuiScaledWidth() >= 600) {
                    List<Component> lines = item.getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL);
                    GL11.glTranslatef(0f, 0f, -1f);
                    guiGraphics.renderTooltip(Minecraft.getInstance().font, Lists.transform(lines, Component::getVisualOrderText), i, j);
                    GL11.glTranslatef(0f, 0f, 1f);
                }

            } else {
                ChestMenu handler = ((ContainerScreen) screen).getMenu();
                Minecraft mc = Minecraft.getInstance();
                LocalPlayer player = mc.player;

                Container inventory = player.getInventory();
                ItemStack item = inventory.getItem(17);

                int x = 20;
                int y = 20;

                // check if block in dev area later.
                for (Component text : item.getTooltipLines(player, TooltipFlag.Default.NORMAL)) {
                    y += 10;
                    guiGraphics.drawString(Minecraft.getInstance().font, text, x, y, 0x000fff);
                }
            }
        }
    }
}