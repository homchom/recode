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
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CodeVaultMenu extends LightweightGuiDescription implements IMenu {

    String[] categories = {
        "All",
        "Soft-Coding",
        "Concepts",
        "Commands",
        "Tools",
        "Game Mechanics",
        "Misc",
        "Var Manipulation",
        "NBS Songs"
    };
    String category = "all";

    List<ItemStack> items = new ArrayList<>();
    ItemScrollablePanel panel;
    CTextField searchBox;
    List<WButton> categoryBtns = new ArrayList<>();

    @Override
    public void open(String... args) throws CommandSyntaxException {
        fetch();
        WPlainPanel root = new WPlainPanel();
        root.setSize(300, 100);

        searchBox = new CTextField(new LiteralText("Search..."));
        searchBox.setMaxLength(100);
        searchBox.setChangedListener(t -> update());
        root.add(searchBox, 110, 0, 250, 0);

        panel = ItemScrollablePanel.with(items);
        panel.setScrollingVertically(TriState.TRUE);
        panel.setScrollingHorizontally(TriState.FALSE);
        root.add(panel, 110, 25, 250, 160);

        int y = 0;
        for (String category : categories) {
            WButton btn = new WButton(new LiteralText(category));
            root.add(btn, 0, y, 100, 22);
            if (Objects.equals(category, "All")) {
                btn.setEnabled(false);
            }
            categoryBtns.add(btn);
            btn.setOnClick(() -> {
                this.category = category.toLowerCase();
                for (WButton cbtn : categoryBtns) {
                    cbtn.setEnabled(true);
                }
                btn.setEnabled(false);
                update();
            });
            y += 21;
        }

        setRootPanel(root);
        root.validate(this);
    }

    private void update() {
        List<ItemStack> filtered = new ArrayList<>();

        String query = searchBox.getText().toLowerCase();

        for (ItemStack item : items) {
            if (item.getName().getString().toLowerCase().contains(query)) {
                if (Objects.equals(category, "all")) {
                    filtered.add(item);
                } else {
                    String category = item.getTag().getString("category");
                    if (Objects.equals(category, this.category)) {
                        filtered.add(item);
                    }
                }
            }
        }

        panel.setItems(filtered);
    }

    private void fetch() {
        CodeUtilitiesServer.requestMessage(new WebMessage("code-vault"), message -> {
            String[] ranks = {"None", "Nobel", "Emperor", "Mythic", "Overlord"};
            String[] plots = {"Basic", "Large", "Massive"};

            HashMap<String, String> categoryConv = new HashMap<>();
            categoryConv.put("&eCommands", "Commands");
            categoryConv.put("&2Note Block Songs","NBS Songs");
            categoryConv.put("&dSoft-Coding","Soft-Coding");
            categoryConv.put("&aTools","Tools");
            categoryConv.put("&eVariable Manipulation","Var Manipulation");
            categoryConv.put("&7Gameplay Mechanics","Game Mechanics");
            categoryConv.put("&3Concepts","Concepts");
            categoryConv.put("&cMisc.","Misc");

            JsonObject content = message.getContent().getAsJsonObject();
            for (Entry<String, JsonElement> k : content.entrySet()) {
                try {
                    JsonArray arr = k.getValue().getAsJsonArray();
                    int plotsize = Integer.parseInt(arr.get(0).getAsString());
                    String category = arr.get(1).getAsString();
                    String templatedata = arr.get(2).getAsString();//todo: add to item nbt
                    String lore = arr.get(3).getAsString();
                    String author = arr.get(4).getAsString();
                    int rank = Integer.parseInt(arr.get(5).getAsString());
                    String name = arr.get(6).getAsString();
                    String material = arr.get(7).getAsString();

                    ItemStack item = new ItemStack(Registry.ITEM.get(new Identifier(material)));
                    item.setCustomName(new LiteralText(name));

                    ListTag loreTag = new ListTag();

                    loreTag.add(StringTag.of(Text.Serializer.toJson(new LiteralText(
                        "§7Created by §a" + author
                    ))));
                    loreTag.add(StringTag.of(Text.Serializer.toJson(new LiteralText(
                        "§2⚐ Category " + category.replaceFirst("&", "§")
                    ))));

                    if (rank == 0) {
                        rank = 1;
                    }
                    if (plotsize == 0) {
                        plotsize = 1;
                    }

                    loreTag.add(StringTag.of(Text.Serializer.toJson(new LiteralText(
                        "§5☐ §7" + ranks[rank - 1] +
                            " §5§l! §7" + plots[plotsize - 1]
                    ))));

                    if (!Objects.equals(lore, "")) {
                        loreTag.add(StringTag.of(
                            Text.Serializer.toJson(new LiteralText("§fDescription:"))));
                        for (String line : lore.split("\n")) {
                            loreTag.add(
                                StringTag.of(Text.Serializer.toJson(new LiteralText(line))));
                        }
                    }

                    item.getSubTag("display").put("Lore", loreTag);
                    item.putSubTag("HideFlags", IntTag.of(127));
                    item.putSubTag("category", StringTag.of(categoryConv.get(category).toLowerCase()));

                    CompoundTag publicBukkitVals = item.getOrCreateSubTag("PublicBukkitValues");
                    publicBukkitVals.putString("hypercube:codetemplatedata",templatedata);
                    items.add(item);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
            CodeUtilities.MC.submit(this::update);
        });
    }
}
