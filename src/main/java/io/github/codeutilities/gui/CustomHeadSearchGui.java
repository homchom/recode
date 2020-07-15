package io.github.codeutilities.gui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.item.CustomHeadCommand;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ItemUtil;
import io.github.codeutilities.util.WebUtil;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WText;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;

public class CustomHeadSearchGui extends LightweightGuiDescription {

    static List<JsonObject> allheads = new ArrayList<>();
    static boolean loaded = false;
    List<JsonObject> heads = new ArrayList<>();
    int headIndex = 0;

    public CustomHeadSearchGui() {
        WGridPanel root = new WGridPanel(1);
        setRootPanel(root);
        root.setSize(256, 240);

        CTextField searchbox = new CTextField(new LiteralText("Search... (0 Heads)"));
        searchbox.setMaxLength(100);

        root.add(searchbox, 0, 0, 256, 0);

        WText loading = new WText(new LiteralText("Loading... (0%)"));
        root.add(loading, 100, 25, 100, 1);

        new Thread(() -> {
            try {
                if (!loaded) {
                    allheads.clear();
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
                        "https://blaze.is-inside.me/fGnAHIz1.json" //actual source: https://headdb.org/api/category/all, had to host it myself to make the json format match
                    };
                    int progress = 0;
                    for (String cat : sources) {
                        String response = WebUtil
                            .getString("" + cat);
                        JsonArray headlist = new Gson().fromJson(response, JsonArray.class);
                        for (JsonElement head : headlist) {
                            allheads.add((JsonObject) head);
                        }
                        searchbox.setSuggestion("Search... (" + allheads.size() + " Heads)");
                        progress++;
                        loading.setText(new LiteralText(
                            "Loading... (" + (progress * 100 / sources.length) + "%)"));
                    }
                    allheads.sort(Comparator.comparing(x -> x.get("name").getAsString()));
                    loaded = true;
                }

                searchbox.setSuggestion("Search... (" + allheads.size() + " Heads)");

                heads = new ArrayList<>(allheads);

                root.remove(loading);

                WGridPanel panel = new WGridPanel(1);
                WScrollPanel scrollPanel = new WScrollPanel(panel);
                scrollPanel.setScrollingVertically(TriState.TRUE);
                scrollPanel.setScrollingHorizontally(TriState.FALSE);

                for (JsonObject head : heads) {
                    ItemStack item = new ItemStack(Items.PLAYER_HEAD);

                    String name = head.get("name").getAsString();
                    String value = head.get("value").getAsString();
                    item.setTag(StringNbtReader.parse(
                        "{display:{Name:\"{\\\"text\\\":\\\"" + name + "\\\"}\"},SkullOwner:{Id:"
                            + CustomHeadCommand.genId()
                            + ",Properties:{textures:[{Value:\"" + value + "\"}]}}}"));
                    CItem i = new CItem(item);
                    i.hover = name;
                    i.setClickListener(() -> {
                        MinecraftClient mc = MinecraftClient.getInstance();
                        if (mc.player.isCreative()) {
                            ItemUtil.giveCreativeItem(item);
                            mc.player
                                .playSound(SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 2,
                                    1);
                        } else {
                            CodeUtilities
                                .chat("You need to be in creative to get heads.", ChatType.FAIL);
                        }
                    });
                    panel.add(i, (int) (headIndex % 14 * 17.8), headIndex / 14 * 18, 17, 18);
                    headIndex++;
                    if (headIndex > 153) {
                        break;
                    }
                }

                WButton button = new WButton(new LiteralText("Load more"));
                button.setOnClick(() -> {
                    int oldIndex = headIndex;
                    do {
                        if (headIndex >= heads.size()) {
                            break;
                        }
                        JsonObject head = heads.get(headIndex);
                        ItemStack item = new ItemStack(Items.PLAYER_HEAD);

                        String name = head.get("name").getAsString();
                        String value = head.get("value").getAsString();
                        try {
                            item.setTag(StringNbtReader.parse(
                                "{display:{Name:\"{\\\"text\\\":\\\"" + name
                                    + "\\\"}\"},SkullOwner:{Id:" + CustomHeadCommand.genId()
                                    + ",Properties:{textures:[{Value:\"" + value + "\"}]}}}"));
                        } catch (CommandSyntaxException e) {
                            e.printStackTrace();
                        }
                        CItem i = new CItem(item);
                        i.hover = name;
                        i.setClickListener(() -> {
                            ItemUtil.giveCreativeItem(item);
                            MinecraftClient.getInstance().player
                                .playSound(SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 2,
                                    1);
                        });
                        panel.add(i, (int) (headIndex % 14 * 17.8), headIndex / 14 * 18, 17, 18);
                        headIndex++;
                    } while (headIndex <= 41 + oldIndex);
                    panel.remove(button);
                    if (headIndex < heads.size()) {
                        panel
                            .add(button, 50, (int) (Math.ceil(((double) headIndex) / 14) * 18), 150,
                                18);
                        button.setLabel(new LiteralText("Load More (" + (heads.size() - headIndex) + ")"));
                    }

                    Field f = null;
                    try {
                        f = scrollPanel.getClass().getDeclaredField("verticalScrollBar");
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    f.setAccessible(true);
                    try {
                        WScrollBar bar = ((WScrollBar) f.get(scrollPanel));
                        bar.onMouseDrag(0, 0, 0);
                        scrollPanel.layout();
                        bar.onMouseDrag(0, 999, 0);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                });
                if (headIndex < heads.size()) {
                    panel
                        .add(button, 50, (int) (Math.ceil(((double) headIndex) / 14) * 18), 150,
                            18);
                    button.setLabel(new LiteralText("Load More (" + (heads.size() - headIndex) + ")"));
                }
                root.add(scrollPanel, 0, 25, 256, 220);

                searchbox.setChangedListener(query -> {
                    root.remove(scrollPanel);

                    if (query.isEmpty()) {
                        heads = new ArrayList<>(allheads);
                    } else {
                        heads.clear();

                        query = query.toLowerCase();
                        for (JsonObject head : allheads) {
                            String name = head.get("name").getAsString();
                            name = name.toLowerCase();

                            if (name.contains(query)) {
                                heads.add(head);
                            }
                        }

                    }

                    Field f = null;
                    try {
                        f = WPanel.class.getDeclaredField("children");
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    f.setAccessible(true);
                    try {
                        List<WWidget> children = ((List) f.get(panel));
                        children.clear();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    headIndex = 0;

                    for (JsonObject head : heads) {
                        ItemStack item = new ItemStack(Items.PLAYER_HEAD);

                        String name = head.get("name").getAsString();
                        String value = head.get("value").getAsString();
                        try {
                            item.setTag(StringNbtReader.parse(
                                "{display:{Name:\"{\\\"text\\\":\\\"" + name
                                    + "\\\"}\"},SkullOwner:{Id:"
                                    + CustomHeadCommand.genId()
                                    + ",Properties:{textures:[{Value:\"" + value + "\"}]}}}"));
                        } catch (CommandSyntaxException e) {
                            e.printStackTrace();
                        }
                        CItem i = new CItem(item);
                        i.hover = name;
                        i.setClickListener(() -> {
                            ItemUtil.giveCreativeItem(item);
                            MinecraftClient.getInstance().player
                                .playSound(SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 2,
                                    1);
                        });
                        panel.add(i, (int) (headIndex % 14 * 17.8), headIndex / 14 * 18, 17, 18);
                        headIndex++;
                        if (headIndex > 153) {
                            break;
                        }
                    }
                    panel.remove(button);
                    if (headIndex < heads.size()) {
                        panel
                            .add(button, 50, (int) (Math.ceil(((double) headIndex) / 14) * 18), 150,
                                18);
                        button.setLabel(new LiteralText("Load More (" + (heads.size() - headIndex) + ")"));
                    }

                    root.add(scrollPanel, 0, 25, 256, 220);
                    panel.setLocation(0, 0);

                    f = null;
                    try {
                        f = scrollPanel.getClass().getDeclaredField("verticalScrollBar");
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    f.setAccessible(true);
                    try {
                        WScrollBar bar = ((WScrollBar) f.get(scrollPanel));
                        bar.onMouseDrag(0, 0, 0);
                        scrollPanel.layout();
                        bar.onMouseDrag(0, 0, 0);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
                if (!searchbox.getText().isEmpty()) {
                    searchbox.setText(searchbox.getText());
                }
            } catch (Exception e) {
                loading.setText(new LiteralText("§cFailed to load!"));
                e.printStackTrace();
            }
        }).start();

        root.validate(this);
    }


}
