package io.github.homchom.recode.mod.features.commands;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.commands.impl.other.PlotsCommand;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.player.chat.*;
import io.github.homchom.recode.sys.renderer.IMenu;
import io.github.homchom.recode.sys.renderer.widgets.*;
import net.minecraft.world.item.ItemStack;

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
        for (ItemStack item : itemlist) {
            Recode.info(item.toString());
            panel.addItem(new PlotItem(item));
        }
        setRootPanel(panel);
        panel.validate(this);
    }
}
