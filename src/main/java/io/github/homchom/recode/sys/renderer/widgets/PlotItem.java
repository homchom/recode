package io.github.homchom.recode.sys.renderer.widgets;

import com.google.gson.*;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.homchom.recode.Recode;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class PlotItem extends CItem {
    public PlotItem(ItemStack stack) {
        super(stack);
    }

    @Override
    public InputResult onClick(int x, int y, int button) {
        ItemStack item = getItems().get(0);
        ListTag lore = item.getOrCreateTagElement("display").getList("Lore", 8);
        JsonObject line = (JsonObject) JsonParser.parseString(lore.getString(lore.size() - 2));
        line = (JsonObject) line.getAsJsonArray("extra").get(0);
        String id = line.get("text").getAsString();
        Recode.MC.player.chat("/join " + id.substring(4));
        return InputResult.IGNORED;
    }
}
