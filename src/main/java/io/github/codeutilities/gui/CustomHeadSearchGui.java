package io.github.codeutilities.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.util.WebUtil;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.Level;

public class CustomHeadSearchGui extends LightweightGuiDescription {

    private static final List<JsonObject> allHeads = new ArrayList<>();
    private static final List<String> categories = new ArrayList<>();
    private static final HashMap<String,Integer> categoryCount = new HashMap<>();
    private final ItemScrollablePanel panel;
    ModConfig config = ModConfig.getConfig();
    private WButton current;
    private String searchQuery = "";
    private final CTextField searchBox;
    private String lastquery = "";

    public CustomHeadSearchGui(String query) {
        WPlainPanel root = new WPlainPanel();
        root.setSize(350, 220);

        searchBox = new CTextField(
            new LiteralText("Search... (" + allHeads.size() + " Heads)"));
        searchBox.setMaxLength(100);
        root.add(searchBox, 100, 0, 250, 0);

        panel = ItemScrollablePanel.with(toItemStack(
            allHeads.subList(0, Math.max(Math.min(allHeads.size(), config.headMenuMaxRender), 1))));
        root.add(panel, 100, 25, 250, 215);

        searchBox.setChangedListener((s -> {
            if (lastquery.equals(s)) {
                return;
            }
            lastquery = s;
            searchQuery = s.toLowerCase();

            updateList();
        }));

        int height = 250 / (categories.size() + 1);

        for (int i = 0; i <= categories.size(); i++) {
            WButton button = new WButton();
            if (i == 0) {
                button.setLabel(new LiteralText("All"));
                button.setEnabled(false);
                current = button;
            } else {
                button.setLabel(new LiteralText(categories.get(i - 1)));
            }
            root.add(button, 0, height * i, 95, height);
            button.setOnClick(() -> {
                current.setEnabled(true);
                button.setEnabled(false);
                current = button;
                updateList();
                String cat = button.getLabel().asString();
                int amount = cat.equals("All") ? allHeads.size() : categoryCount.get(cat);
                searchBox.setSuggestion(new LiteralText("Search... (" + amount + " Heads)"));
            });
        }

        setRootPanel(root);
        root.validate(this);
        searchBox.setText(query);
    }

    public static void load() {
        new Thread(() -> {
            allHeads.clear();
            String[] sources = {
                "https://minecraft-heads.com/scripts/api.php?cat=alphabet&tags=true",
                "https://minecraft-heads.com/scripts/api.php?cat=animals&tags=true",
                "https://minecraft-heads.com/scripts/api.php?cat=blocks&tags=true",
                "https://minecraft-heads.com/scripts/api.php?cat=decoration&tags=true",
                "https://minecraft-heads.com/scripts/api.php?cat=humans&tags=true",
                "https://minecraft-heads.com/scripts/api.php?cat=humanoid&tags=true",
                "https://minecraft-heads.com/scripts/api.php?cat=miscellaneous&tags=true",
                "https://minecraft-heads.com/scripts/api.php?cat=monsters&tags=true",
                "https://minecraft-heads.com/scripts/api.php?cat=plants&tags=true",
                "https://minecraft-heads.com/scripts/api.php?cat=food-drinks&tags=true"
            };
            for (String db : sources) {
                try {
                    String response = WebUtil.getString(db);
                    String cat = StringUtils.substringBetween(db, "?cat=", "&tags=true");

                    if (cat.equals("food-drinks")) {
                        cat = "food & drinks";
                    }

                    cat = WordUtils.capitalize(cat);
                    categories.add(cat);

                    int amount = 0;

                    JsonArray heads = new JsonParser().parse(response).getAsJsonArray();
                    for (JsonElement head : heads) {
                        JsonObject h = head.getAsJsonObject();
                        h.addProperty("category", cat);
                        if (h.get("tags").isJsonNull()) h.addProperty("tags","None");
                        allHeads.add(h);
                        amount++;
                    }
                    categoryCount.put(cat,amount);
                } catch (IOException | JsonSyntaxException exception) {
                    exception.printStackTrace();
                }
            }
            allHeads.sort(Comparator.comparing(x -> x.get("name").getAsString()));
        }).start();
    }

    public void updateList() {
        List<JsonObject> selected = new ArrayList<>();
        String cat = current.getLabel().asString();

        if (searchQuery.isEmpty()) {
            if (cat.equals("All")) {
                selected = allHeads
                    .subList(0, Math.max(Math.min(allHeads.size(), config.headMenuMaxRender), 1));
            } else {
                for (JsonObject object : allHeads) {
                    if (cat.equals("All") || object.get("category").getAsString().equals(cat)) {
                        selected.add(object);
                        if (selected.size() >= config.headMenuMaxRender) {
                            break;
                        }
                    }
                }
            }
        } else {
            for (JsonObject object : allHeads) {
                if (cat.equals("All") || object.get("category").getAsString().equals(cat)) {
                    if (object.get("name").getAsString().toLowerCase().contains(searchQuery) || object.get("tags").getAsString().toLowerCase().contains(searchQuery)) {
                        selected.add(object);
                        if (selected.size() >= config.headMenuMaxRender) {
                            break;
                        }
                    }
                }
            }
        }
        panel.setItems(toItemStack(selected));
    }

    private List<ItemStack> toItemStack(List<JsonObject> set) {
        List<ItemStack> items = new ArrayList<>();

        try {
            for (JsonObject head : set) {
                ItemStack item = new ItemStack(Items.PLAYER_HEAD);
                String name = head.get("name").getAsString();
                String value = head.get("value").getAsString();
                String tags = head.get("tags").getAsString();

                CompoundTag nbt = new CompoundTag();
                CompoundTag display = new CompoundTag();
                display.putString("Name", "{\"text\":" + StringTag.escape("§e" + name) + "}");

                ListTag lore = new ListTag();

                lore.add(StringTag.of("{\"text\":\"§7Tags:\"}"));

                for (String tag : tags.split(",")) {
                    lore.add(StringTag.of("{\"text\":" + StringTag.escape("§7-" + tag) +"}"));
                }

                display.put("Lore",lore);

                nbt.put("display", display);
                CompoundTag SkullOwner = new CompoundTag();
                SkullOwner.putIntArray("Id", Arrays
                    .asList(CodeUtilities.rng.nextInt(), CodeUtilities.rng.nextInt(),
                        CodeUtilities.rng.nextInt(), CodeUtilities.rng.nextInt()));
                CompoundTag Properties = new CompoundTag();
                ListTag textures = new ListTag();
                CompoundTag index1 = new CompoundTag();
                index1.putString("Value", value);
                textures.add(index1);
                Properties.put("textures", textures);
                SkullOwner.put("Properties", Properties);
                nbt.put("SkullOwner", SkullOwner);
                item.setTag(nbt);
                items.add(item);
            }
        } catch (Exception e) {
            CodeUtilities.log(Level.ERROR, "Error in toItemStack!!!");
            CodeUtilities.log(Level.ERROR, "Stack trace:");
            e.printStackTrace();
        }
        return items;
    }

}
