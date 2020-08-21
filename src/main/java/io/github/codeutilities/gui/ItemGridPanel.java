package io.github.codeutilities.gui;

import io.github.cottonmc.cotton.gui.widget.*;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemGridPanel extends WPlainPanel {

    public ItemGridPanel() {
        super();
    }

    public void addItem(ItemStack item) {
        int index = getItems().size();
        add(new ClickableGiveItem(item), (int) (index % 14 * 17), index / 14 * 18);
    }

    public void addItem(CItem item) {
        int index = getItems().size();
        add(item, (int) (index % 14 * 17), index / 14 * 18, 17, 18);
    }

    public List<WWidget> getItems() {
        return this.children;
    }
}
