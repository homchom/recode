package io.github.codeutilities.util.render.gui.widgets;

import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemScrollablePanel extends WScrollPanel {

    private final ItemGridPanel itemGrid;

    private ItemScrollablePanel(ItemGridPanel grid, List<ItemStack> items) {
        super(grid);
        this.itemGrid = grid;

        for (ItemStack stack : items) {
            itemGrid.addItem(stack);
        }
    }

    public static ItemScrollablePanel with(List<ItemStack> items) {
        return new ItemScrollablePanel(new ItemGridPanel(), items);
    }

    public void setItems(List<ItemStack> items) {
        itemGrid.getItems().clear();
        itemGrid.setSize(0, 0);
        horizontalScrollBar.setValue(0);

        for (ItemStack item : items) {
            itemGrid.addItem(item);
        }
        layout();
    }

    public ItemGridPanel getItemGrid() {
        return itemGrid;
    }
}
