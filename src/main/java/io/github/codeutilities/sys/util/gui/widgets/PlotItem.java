package io.github.codeutilities.sys.util.gui.widgets;

import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;

public class PlotItem extends CItem {
    public PlotItem(ItemStack stack) {
        super(stack);
    }

    @Override
    public void onClick(int x, int y, int button) {
        ItemStack item = getItems().get(0);
        ListTag lore = item.getOrCreateSubTag("display").getList("Lore", 8);
        JsonObject line = (JsonObject) CodeUtilities.JSON_PARSER.parse(lore.getString(lore.size() - 2));
        line = (JsonObject) line.getAsJsonArray("extra").get(0);
        String id = line.get("text").getAsString();
        CodeUtilities.MC.player.sendChatMessage("/join " + id.substring(4));
    }
}
