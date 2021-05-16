package io.github.codeutilities.util.gui;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import net.minecraft.client.MinecraftClient;

public interface IMenu {
    default void scheduleOpenGui(LightweightGuiDescription gui, String... args) {
        this.open(args);
        MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().openScreen(new CottonClientScreen(gui)));
    }

    void open(String... args);
}
