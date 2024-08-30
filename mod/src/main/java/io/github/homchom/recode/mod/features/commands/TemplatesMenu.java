package io.github.homchom.recode.mod.features.commands;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.homchom.recode.sys.hypercube.templates.TemplateItem;
import io.github.homchom.recode.sys.hypercube.templates.TemplateStorageHandler;
import io.github.homchom.recode.sys.renderer.IMenu;
import io.github.homchom.recode.sys.renderer.widgets.ItemScrollablePanel;
import net.minecraft.world.item.ItemStack;

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
        root.setInsets(Insets.ROOT_PANEL);
    }
}
