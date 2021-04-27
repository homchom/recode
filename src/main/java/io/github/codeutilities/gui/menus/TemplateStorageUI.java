package io.github.codeutilities.gui.menus;

import io.github.codeutilities.gui.widgets.ItemScrollablePanel;
import io.github.codeutilities.template.TemplateItem;
import io.github.codeutilities.template.TemplateStorageHandler;
import io.github.codeutilities.util.IMenu;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TemplateStorageUI extends LightweightGuiDescription implements IMenu {

    public TemplateStorageUI() {
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
