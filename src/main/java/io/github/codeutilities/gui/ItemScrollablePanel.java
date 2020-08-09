package io.github.codeutilities.gui;

import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.minecraft.item.ItemStack;

public class ItemScrollablePanel extends WScrollPanel {

    private final WGridPanel itemPanel = (WGridPanel) this.children.get(0);

    public ItemScrollablePanel(List<ItemStack> items) {
        super(new WGridPanel(1));

        setItems(items);
    }

    public void setItems(List<ItemStack> items) {
        try {
            Class cl = WPanel.class;
            Field children = cl.getDeclaredField("children");
            children.setAccessible(true);
            ((List) children.get(itemPanel)).clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        itemPanel.setSize(0, 0);
        horizontalScrollBar.setValue(0);

        int renderIndex = 0;
        for (ItemStack item : items) {
            ClickableGiveItem i = new ClickableGiveItem(item);
            i.setScale(1.5F);
            itemPanel.add(i, (int) (renderIndex % 12 * 20), renderIndex / 12 * 20, 20, 20);
            renderIndex++;
        }
        layout();
    }
}
