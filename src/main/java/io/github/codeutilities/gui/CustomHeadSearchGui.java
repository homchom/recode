package io.github.codeutilities.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.util.IManager;
import io.github.codeutilities.util.WebUtil;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CustomHeadSearchGui extends LightweightGuiDescription implements IManager<JsonObject> {

    private final List<JsonObject> registeredHeads = new ArrayList<>();
    private static CustomHeadSearchGui instance;

    private ItemScrollablePanel panel;
    ModConfig config = ModConfig.getConfig();

    public CustomHeadSearchGui() {
        instance = this;
    }

    public void loadGui() {
        WPlainPanel root = new WPlainPanel();
        root.setSize(256, 240);

        CTextField searchBox = new CTextField(
                new LiteralText("Search... (" + registeredHeads.size() + " Heads)"));
        searchBox.setMaxLength(100);
        root.add(searchBox, 0, 0, 256, 0);

        panel = ItemScrollablePanel.with(toItemStack(registeredHeads.subList(0, Math.max(Math.min(registeredHeads.size(), config.headMenuMaxRender), 1))));
        root.add(panel, 0, 25, 256, 235);

        final String[] lastQuery = {""};//intellij wants me to do this, don't ask me why
        searchBox.setChangedListener((s -> {
            if (lastQuery[0].equals(s)) {
                return;
            }
            lastQuery[0] = s;
            s = s.toLowerCase();
            List<JsonObject> selected = new ArrayList<>();

            if (s.isEmpty()) {
                selected = registeredHeads
                        .subList(0, Math.max(Math.min(registeredHeads.size(), config.headMenuMaxRender), 1));
            } else {
                for (JsonObject object : registeredHeads) {
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

    private List<ItemStack> toItemStack(List<JsonObject> set) {
        List<ItemStack> items = new ArrayList<>();

        try {
            for (JsonObject head : set) {
                ItemStack item = new ItemStack(Items.PLAYER_HEAD);
                String name = head.get("name").getAsString();
                String value = head.get("value").getAsString();

                CompoundTag nbt = new CompoundTag();
                CompoundTag display = new CompoundTag();
                display.putString("Name", "{\"text\":\"" + name + "\"}");
                nbt.put("display", display);
                CompoundTag SkullOwner = new CompoundTag();
                SkullOwner.putIntArray("Id", Arrays
                        .asList(CodeUtilities.RANDOM.nextInt(), CodeUtilities.RANDOM.nextInt(),
                                CodeUtilities.RANDOM.nextInt(), CodeUtilities.RANDOM.nextInt()));
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

    @Override
    public void initialize() {
        registeredHeads.clear();

        CompletableFuture.runAsync(() -> {
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
                    "http://redirectrepl.blazemcworld.repl.co/?src=https://blaze.is-inside.me/fGnAHIz1.json"
                    //actual source: https://headdb.org/api/category/all, had to host it myself to make the json format match
            };
            for (String db : sources) {
                try {
                    String response = WebUtil.getString(db);

                    JsonElement parse = CodeUtilities.JSON_PARSER.parse(response);
                    JsonArray heads = parse.getAsJsonArray();

                    for (JsonElement head : heads) {
                        if (head instanceof JsonObject) {
                            this.register(head.getAsJsonObject());
                        }
                    }
                } catch (IOException | JsonSyntaxException exception) {
                    exception.printStackTrace();
                }
            }
            registeredHeads.sort(Comparator.comparing(x -> x.get("name").getAsString()));
        });
    }

    @Override
    public void register(JsonObject object) {
        this.registeredHeads.add(object);
    }

    @Override
    public List<JsonObject> getRegistered() {
        return this.registeredHeads;
    }

    public static CustomHeadSearchGui getInstance() {
        return instance;
    }
}
