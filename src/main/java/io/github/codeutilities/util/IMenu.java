package io.github.codeutilities.util;

import io.github.codeutilities.CodeUtilities;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public interface IMenu {
    default void scheduleOpenGui(LightweightGuiDescription gui, String... args) {
        this.open(args);
        MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().openScreen(new CottonClientScreen(gui)));
    }

    void open(String... args);
}
