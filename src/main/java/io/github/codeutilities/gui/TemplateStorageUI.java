package io.github.codeutilities.gui;

import io.github.codeutilities.template.*;
import io.github.codeutilities.util.*;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.*;

public class TemplateStorageUI extends LightweightGuiDescription {


    public TemplateStorageUI() {
        WGridPanel root = new WGridPanel(1);
        setRootPanel(root);
        root.setSize(256, 90);
        int index = 0;
        for (TemplateItem item : TemplateStorageHandler.getTemplates()) {
            ClickableGiveItem i = new ClickableGiveItem(item.getStack());
            root.add(i, (int) (index % 14 * 17.8), index / 14 * 18, 17, 18);
            index++;
        }
        root.validate(this);
    }


}
