package io.github.homchom.recode.mod.features.commands;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.features.commands.nbs.*;
import io.github.homchom.recode.mod.features.social.tab.RecodeServer;
import io.github.homchom.recode.sys.hypercube.templates.TemplateUtils;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import io.github.homchom.recode.sys.renderer.IMenu;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.*;
import net.minecraft.world.item.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.*;

public class NbsSearchMenu extends LightweightGuiDescription implements IMenu {

    public final String query;
    private final SoundEvent[] instrumentids = {
        SoundEvents.NOTE_BLOCK_HARP,
        SoundEvents.NOTE_BLOCK_BASS,
        SoundEvents.NOTE_BLOCK_BASEDRUM,
        SoundEvents.NOTE_BLOCK_SNARE,
        SoundEvents.NOTE_BLOCK_HAT,
        SoundEvents.NOTE_BLOCK_GUITAR,
        SoundEvents.NOTE_BLOCK_FLUTE,
        SoundEvents.NOTE_BLOCK_BELL,
        SoundEvents.NOTE_BLOCK_CHIME,
        SoundEvents.NOTE_BLOCK_XYLOPHONE,
        SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE,
        SoundEvents.NOTE_BLOCK_COW_BELL,
        SoundEvents.NOTE_BLOCK_DIDGERIDOO,
        SoundEvents.NOTE_BLOCK_BIT
    };
    int previewId = -1;

    public NbsSearchMenu(String query) {
        this.query = query;
    }

    @Override
    public void open(String... args) throws CommandSyntaxException {
        Minecraft mc = Recode.MC;
        WPlainPanel root = new WPlainPanel();
        root.setSize(300, 240);

        WText queryField = new WText(new TextComponent("§l§nSearch Results for: " + query));
        root.add(queryField, 0, 0, 300, 0);

        WPlainPanel ppanel = new WPlainPanel();

        WText loading = new WText(new TextComponent("§lLoading Results..."));

        ppanel.add(loading, 85, 50, 300, 0);

        WScrollPanel spanel = new WScrollPanel(ppanel);

        Recode.EXECUTOR.submit(() -> {
            try {
                String sresults = RecodeServer.requestURL(
                    "https://untitled-57qvszfgg28u.runkit.sh/search?query=" + URLEncoder
                        .encode(query, "UTF-8"));

                JsonArray results = JsonParser.parseString(sresults).getAsJsonArray();

                mc.execute(() -> {
                    ppanel.remove(loading);

                    int y = 5;

                    for (JsonElement resulte : results) {
                        JsonObject e = resulte.getAsJsonObject();

                        int id = e.get("id").getAsInt();
                        String duration = e.get("duration").getAsString();
                        WText title = new WText(new TextComponent(e.get("title").getAsString()));
                        WText description = new WText(
                            new TextComponent(duration + " §8-§r " + e.get("composer").getAsString()));

                        ppanel.add(title, 0, y, 999, 10);
                        ppanel.add(description, 0, y + 10, 999, 10);

                        WButton download = new WButton(new TextComponent("§l↓"));

                        ppanel.add(download, 270, y, 20, 20);

                        download.setOnClick(() -> {
                            download.setLabel(new TextComponent("..."));
                            Recode.EXECUTOR
                                .submit(() -> {
                                    String notes = RecodeServer.requestURL(
                                        "https://untitled-57qvszfgg28u.runkit.sh/download?format=mcnbs&id="
                                            + id);
                                    String[] notearr = notes.split("=");
                                    int length = Integer
                                        .parseInt(notearr[notearr.length - 1].split(":")[0]);
                                    SongData d = new SongData("Song " + id, "CodeUtilities", 20f,
                                        length, notes, "", "", 1, 0, 0);

                                    String code = new NBSToTemplate(d).convert();

                                    ItemStack stack = new ItemStack(Items.NOTE_BLOCK);
                                    TemplateUtils
                                        .compressTemplateNBT(stack, d.getName(), d.getAuthor(), code);

                                    stack.setHoverName(
                                        new TextComponent("§d" + e.get("title").getAsString()));

                                    ItemUtil.giveCreativeItem(stack, true);
                                    download.setLabel(new TextComponent("§l↓"));
                                });
                        });

                        WButton preview = new WButton(new TextComponent("▶"));

                        ppanel.add(preview, 250, y, 20, 20);

                        preview.setOnClick(() -> {
                            if (previewId != id) {
                                previewId = id;
                                preview.setLabel(new TextComponent("..."));
                                Recode.EXECUTOR
                                    .submit(() -> {
                                        String snotes = RecodeServer.requestURL(
                                            "https://untitled-57qvszfgg28u.runkit.sh/download?format=mcnbs&id="
                                                + id);
                                        List<String> notes = new ArrayList<>(
                                            Arrays.asList(snotes.split("=")));

                                        preview.setLabel(new TextComponent("■"));

                                        int[] tick = {0};
                                        int[] index = {0};
                                        ScheduledExecutorService scheduler = Executors
                                            .newScheduledThreadPool(1);
                                        scheduler.scheduleAtFixedRate(() -> Recode.MC.submit(() -> { //apparently playing sounds non-sync can crash the game
                                            if (previewId != id
                                                || mc.screen == null) {
                                                scheduler.shutdown();
                                                preview.setLabel(new TextComponent("▶"));
                                                return;
                                            }
                                            if (notes.get(index[0])
                                                .startsWith(tick[0] + ":")) {

                                                String line = notes.get(index[0])
                                                    .split(":")[1];

                                                for (String n : line.split(";")) {
                                                    String[] d = n.split(",");
                                                    int instrumentid = Integer
                                                        .parseInt(d[0]);
                                                    float pitch =
                                                        (float) Integer.parseInt(d[1])
                                                            / 1000;
                                                    float panning =
                                                        (float) Integer.parseInt(d[2]) / 100;
                                                    mc.player
                                                        .playSound(
                                                            instrumentids[instrumentid - 1],
                                                            panning, pitch);
                                                }

                                                index[0]++;
                                            }
                                            tick[0]++;
                                        }), 0, 1000 / 20, TimeUnit.MILLISECONDS);
                                    });
                            } else {
                                previewId = -1;
                            }
                        });

                        y += 25;
                    }

                    spanel.layout();
                });
            } catch (UnsupportedEncodingException err) {
                err.printStackTrace();
                loading.setText(new TextComponent("Error"));
                ChatUtil.sendMessage("Error");
            }
        });

        spanel.setScrollingHorizontally(TriState.FALSE);
        spanel.setScrollingVertically(TriState.TRUE);
        root.add(spanel, 0, 10, 300, 230);
        setRootPanel(root);
        root.validate(this);
    }
}
