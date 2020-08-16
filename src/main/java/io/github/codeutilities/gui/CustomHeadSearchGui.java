package io.github.codeutilities.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.util.WebUtil;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;

public class CustomHeadSearchGui extends LightweightGuiDescription {

    private static final List<JsonObject> allHeads = new ArrayList<>();
    private final ItemScrollablePanel panel;
    ModConfig config = ModConfig.getConfig();

    public CustomHeadSearchGui() {
        WPlainPanel root = new WPlainPanel();
        root.setSize(256, 240);
        

        CTextField searchBox = new CTextField(
            new LiteralText("Search... (" + allHeads.size() + " Heads)"));
        searchBox.setMaxLength(100);
        root.add(searchBox, 0, 0, 256, 0);

        panel = new ItemScrollablePanel(toItemStack(
            allHeads.subList(0, Math.max(Math.min(allHeads.size(), config.headMenuMaxRender), 1))));
        root.add(panel, 0, 25, 256, 235);

        searchBox.setChangedListener((s -> {
            s = s.toLowerCase();
            List<JsonObject> selected = new ArrayList<>();

            if (s.isEmpty()) {
                selected = allHeads
                    .subList(0, Math.max(Math.min(allHeads.size(), config.headMenuMaxRender), 1));
            } else {
                for (JsonObject object : allHeads) {
                    if (object.get("name").getAsString().toLowerCase().contains(s)
                        && selected.size() <= config.headMenuMaxRender) {
                        selected.add(object);
                    }
                }
            }
            panel.setItems(toItemStack(selected));
        }));

        setRootPanel(root);
        root.validate(this);
    }

    public static void load() {
        new Thread(() -> {
            allHeads.clear();
            String[] sources = {
                "https://minecraft-heads.com/scripts/api.php?cat=alphabet",
                "https://minecraft-heads.com/scripts/api.php?cat=animals",
                "https://minecraft-heads.com/scripts/api.php?cat=blocks",
                "https://minecraft-heads.com/scripts/api.php?cat=decoration",
                "https://minecraft-heads.com/scripts/api.php?cat=humans",
                "https://minecraft-heads.com/scripts/api.php?cat=humanoid",
                "https://minecraft-heads.com/scripts/api.php?cat=miscellaneous",
                "https://minecraft-heads.com/scripts/api.php?cat=monsters",
                "https://minecraft-heads.com/scripts/api.php?cat=plants",
                "https://minecraft-heads.com/scripts/api.php?cat=food-drinks",
                "https://blaze.is-inside.me/fGnAHIz1.json"
                //actual source: https://headdb.org/api/category/all, had to host it myself to make the json format match
            };
            for (String db : sources) {
                String response;
                try {
                    response = WebUtil
                        .getString("http://redirectrepl.blazemcworld.repl.co/?src=" + db);
                } catch (IOException exception) {
                    exception.printStackTrace();
                    continue;
                }

                JsonArray heads = new JsonParser().parse(response).getAsJsonArray();
                for (JsonElement head : heads) {
                    allHeads.add(head.getAsJsonObject());
                }
            }
            allHeads.sort(Comparator.comparing(x -> x.get("name").getAsString()));
        }).start();
    }

    private List<ItemStack> toItemStack(List<JsonObject> set) {
        List<ItemStack> items = new ArrayList<>();

        for (JsonObject head : set) {
            ItemStack item = new ItemStack(Items.PLAYER_HEAD);
            String name = head.get("name").getAsString();
            String value = head.get("value").getAsString();

            CompoundTag nbt = new CompoundTag();
            CompoundTag display = new CompoundTag();
            CompoundTag Name = new CompoundTag();
            display.putString("Name", "{\"text\":\"" + name + "\"}");
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
        return items;
    }

}
