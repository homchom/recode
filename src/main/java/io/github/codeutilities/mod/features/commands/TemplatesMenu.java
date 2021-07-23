package io.github.codeutilities.mod.features.commands;

import io.github.codeutilities.sys.renderer.IMenu;
import io.github.codeutilities.sys.renderer.widgets.ItemScrollablePanel;
import io.github.codeutilities.sys.hypercube.templates.TemplateItem;
import io.github.codeutilities.sys.hypercube.templates.TemplateStorageHandler;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TemplatesMenu extends LightweightGuiDescription implements IMenu {

    public TemplatesMenu() {
    }

    @Override
    public void open(String... args) {
        List<ItemStack> items = new ArrayList<>();
        for (TemplateItem item : TemplateStorageHandler.getInstance().getRegistered()) {
            items.add(item.getStack());
        }
        WPlainPanel root = new WPlainPanel();
        ItemScrollablePanel panel = ItemScrollablePanel.with(items);
        root.setSize(256, 90);
        panel.setSize(256, 90);

        root.add(panel, 0, 0, 256, 90);

        setRootPanel(root);
        root.validate(this);
    }
}
