package io.github.codeutilities.mod.features.commands;

import io.github.codeutilities.mod.commands.impl.other.PlotsCommand;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import io.github.codeutilities.sys.renderer.IMenu;
import io.github.codeutilities.sys.renderer.widgets.ItemGridPanel;
import io.github.codeutilities.sys.renderer.widgets.PlotItem;
import io.github.codeutilities.sys.player.DFInfo;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import net.minecraft.item.ItemStack;

import java.util.List;

public class PlotsMenu extends LightweightGuiDescription implements IMenu {

    public PlotsMenu() {
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
