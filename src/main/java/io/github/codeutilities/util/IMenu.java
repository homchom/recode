package io.github.codeutilities.util;

import io.github.codeutilities.CodeUtilities;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import net.minecraft.client.MinecraftClient;

public interface IMenu {
    // Don't do this. Opening guis from others threads on a synchronous game is NOT GOOD.
    default void openAsync(LightweightGuiDescription gui) {
        CodeUtilities.EXECUTOR.submit(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MinecraftClient.getInstance().openScreen(new CottonClientScreen(gui));
        });
    }

    void open(String... args);
}
