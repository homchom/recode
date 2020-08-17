package io.github.codeutilities.gui;

import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import java.util.List;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.reflect.FieldUtils;

public class ItemScrollablePanel extends WScrollPanel {

    private final WGridPanel itemPanel = (WGridPanel) this.children.get(0);

    public ItemScrollablePanel(List<ItemStack> items) {
        super(new WGridPanel(1));

        setItems(items);
    }

    public void setItems(List<ItemStack> items) {
        try {
            Object uncheckedChildren = FieldUtils
                .readField(itemPanel, "children", true);
            if (uncheckedChildren instanceof List) {
                List children = (List) uncheckedChildren;
                children.clear();
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        itemPanel.setSize(0, 0);
        horizontalScrollBar.setValue(0);

        int renderIndex = 0;
        for (ItemStack item : items) {
            ClickableGiveItem i = new ClickableGiveItem(item);
            itemPanel.add(i, (int) (renderIndex % 14 * 17.8), renderIndex / 14 * 18, 17, 18);
            renderIndex++;
        }
        layout();
    }
}
