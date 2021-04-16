package io.github.codeutilities.gui;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.IMenu;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import net.minecraft.client.MinecraftClient;

public class ColorPickerMenu extends LightweightGuiDescription implements IMenu {

    @Override
    public void open(String... args) {
        MinecraftClient mc = CodeUtilities.MC;
        WGridPanel root = new WGridPanel(1);
        root.setSize(256, 240);
        //TODO: color picker

        setRootPanel(root);
        root.validate(this);
    }
}
