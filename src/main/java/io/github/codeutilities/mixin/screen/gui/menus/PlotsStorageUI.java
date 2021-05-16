package io.github.codeutilities.mixin.screen.gui.menus;

import io.github.codeutilities.commands.impl.util.PlotsCommand;
import io.github.codeutilities.mixin.screen.gui.IMenu;
import io.github.codeutilities.mixin.screen.gui.widgets.ItemGridPanel;
import io.github.codeutilities.mixin.screen.gui.widgets.PlotItem;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.codeutilities.util.networking.DFInfo;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import net.minecraft.item.ItemStack;

import java.util.List;

public class PlotsStorageUI extends LightweightGuiDescription implements IMenu {

    public PlotsStorageUI() {
    }

    @Override
    public void open(String... args) {
        ItemGridPanel panel = new ItemGridPanel();
        List<ItemStack> itemlist = DFInfo.isInBeta ? PlotsCommand.betaItems : PlotsCommand.items;
        if (itemlist == null || itemlist.size() == 0) {
            ChatUtil.sendMessage("Unable to load plots, please open the menu from the item so it can cache the plots.", ChatType.FAIL);
            return;
        }
        for (ItemStack item : itemlist) panel.addItem(new PlotItem(item));
        setRootPanel(panel);
        panel.validate(this);
    }
}
