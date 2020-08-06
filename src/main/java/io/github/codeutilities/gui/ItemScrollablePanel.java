package io.github.codeutilities.gui;

import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;

public class ItemScrollablePanel extends WScrollPanel {

    // Jank, I know.
    private final WGridPanel itemPanel = (WGridPanel) this.children.get(0);
    private final List<ClickableGiveItem> giveItems = new ArrayList<>();

    public ItemScrollablePanel(List<ItemStack> items) {
        super(new WGridPanel(1));

        setItems(items);
    }

    public void setItems(List<ItemStack> items) {
        for (ClickableGiveItem giveItem : giveItems) {
            itemPanel.remove(giveItem);
        }
        itemPanel.setSize(0, 0);
        horizontalScrollBar.setValue(0);

        int renderIndex = 0;
        for (ItemStack item : items) {
            ClickableGiveItem i = new ClickableGiveItem(item);
            giveItems.add(i);
            itemPanel.add(i, (int) (renderIndex % 14 * 17.8), renderIndex / 14 * 18, 17, 18);
            renderIndex++;
        }
        layout();
    }
}
