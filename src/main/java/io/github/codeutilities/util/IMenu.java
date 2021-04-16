package io.github.codeutilities.util;

import io.github.codeutilities.CodeUtilities;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;

public interface IMenu {

    default void openAsync(LightweightGuiDescription gui, String... args) {
        this.open(args);

        CottonClientScreen cottonClientScreen = new CottonClientScreen(gui);
        CodeUtilities.MC.openScreen(cottonClientScreen);
    }

    void open(String... args);
}
