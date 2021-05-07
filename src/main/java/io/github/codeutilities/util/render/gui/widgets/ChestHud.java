package io.github.codeutilities.util.render.gui.widgets;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.CodeUtilsConfig;
import io.github.codeutilities.util.networking.DFInfo;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;

public class ChestHud {
    public static void register() {
        ScreenEvents.AFTER_INIT.register(ChestHud::afterInit);
    }

    private static void afterInit(MinecraftClient client, Screen screen, int windowWidth, int windowHeight) {
        if (screen instanceof GenericContainerScreen) {
            ScreenEvents.afterRender(screen).register(ChestHud::afterContainerRender);
        }
    }


    private static void afterContainerRender(Screen screen, MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        GenericContainerScreenHandler handler = ((GenericContainerScreen) screen).getScreenHandler();
        MinecraftClient mc = CodeUtilities.MC;
        ClientPlayerEntity player = mc.player;

        Inventory inventory = player.inventory;
        ItemStack item = inventory.getStack(17);

        Integer x = 20;
        Integer y = 20;


        if (DFInfo.currentState == DFInfo.State.DEV) {
            if (CodeUtilsConfig.getBool("chestToolTip")) {
                for (Text text : item.getTooltip(player, TooltipContext.Default.NORMAL)) {
                    y += 10;
                    MinecraftClient.getInstance().textRenderer.draw(matrices, text, x, y, 0x000fff);

                }
            }
        }

    }
}