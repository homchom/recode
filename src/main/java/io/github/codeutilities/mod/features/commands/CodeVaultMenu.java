package io.github.codeutilities.mod.features.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.features.social.tab.CodeUtilitiesServer;
import io.github.codeutilities.mod.features.social.tab.WebMessage;
import io.github.codeutilities.sys.renderer.IMenu;
import io.github.codeutilities.sys.renderer.widgets.CTextField;
import io.github.codeutilities.sys.renderer.widgets.ItemScrollablePanel;
import io.github.codeutilities.sys.util.ItemUtil;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CodeVaultMenu extends LightweightGuiDescription implements IMenu {

    String[] categories = {
        "Soft-Coding",
        "Concepts",
        "Commands",
        "Tools",
        "Game Mechanics",
        "Misc",
        "Var Manipulation",
        "NBS Songs"
    };

    List<ItemStack> items = new ArrayList<>();
    ItemScrollablePanel panel;

    @Override
    public void open(String... args) throws CommandSyntaxException {
        fetch();
        WPlainPanel root = new WPlainPanel();
        root.setSize(300, 100);

        WTextField searchBox = new WTextField(
            new LiteralText("Search..."));
        searchBox.setMaxLength(100);//todo: make changing the query update the items
        root.add(searchBox, 110, 0, 250, 0);

        panel = ItemScrollablePanel.with(items);
        panel.setScrollingVertically(TriState.TRUE);
        panel.setScrollingHorizontally(TriState.FALSE);
        root.add(panel, 110, 25, 250, 140);

        int y = 0;
        for (String category : categories) {
            WButton btn = new WButton(new LiteralText(category));
            root.add(btn,0,y,100,22);
            //todo: make category buttons do something
            y+=21;
        }

        setRootPanel(root);
        root.validate(this);
    }

    private void fetch() {
        CodeUtilitiesServer.requestMessage(new WebMessage("code-vault"), message -> {
            JsonObject content = message.getContent().getAsJsonObject();
            for (Entry<String, JsonElement> k : content.entrySet()) {
                try {
                    JsonArray arr = k.getValue().getAsJsonArray();
                    String id = k.getKey();//todo: add to item lore
                    String plotsize = arr.get(0).getAsString();
                    String category = arr.get(1).getAsString();
                    String templatedata = arr.get(2).getAsString();//todo: add to item nbt
                    String lore = arr.get(3).getAsString();//todo: add
                    String author = arr.get(4).getAsString();
                    String rank = arr.get(5).getAsString();
                    String name = arr.get(6).getAsString();
                    String material = arr.get(7).getAsString();

                    ItemStack item = new ItemStack(Registry.ITEM.get(new Identifier(material)));
                    item.setCustomName(new LiteralText(name));

                    List<String> ilore = new ArrayList<>();

                    ilore.add("§7Created by §a" + author);
                    ilore.add("§2⚐ Category " + category);
                    ilore.add("§5☐ §7" + rank + " §5§l! §7" + plotsize);//todo: translate rank & plotsize to string

                    item = ItemUtil.setLore(item, ilore.toArray(new String[0]));

                    items.add(item);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
            CodeUtilities.MC.submit(() -> panel.setItems(items));
        });
    }
}
