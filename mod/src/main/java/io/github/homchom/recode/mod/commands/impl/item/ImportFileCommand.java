package io.github.homchom.recode.mod.commands.impl.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.ModConstants;
import io.github.homchom.recode.io.NativeIO;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.sys.hypercube.templates.CompressionUtil;
import io.github.homchom.recode.sys.hypercube.templates.TemplateUtil;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import io.github.homchom.recode.sys.util.ItemUtil;
import io.github.homchom.recode.ui.screen.DummyScreen;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// TODO: clean up further
public class ImportFileCommand extends Command {
    private final String TITLE = "Pick a file";

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        cd.register(ArgBuilder.literal("importfile")
                .executes(ctx -> {
                    if (!isCreative(mc)) return -1;
                    ChatUtil.sendMessage("Opening File Picker", ChatType.INFO_BLUE);
                    Minecraft.getInstance().tell(this::openFilePickerScreen);
                    return 1;
                })
        );
    }

    private void openFilePickerScreen() {
        var screen = new DummyScreen(Component.text(TITLE), true);
        Minecraft.getInstance().setScreen(screen);

        LegacyRecode.executor.execute(() -> {
            var paths = NativeIO.pickMultipleFiles(TITLE);
            Minecraft.getInstance().execute(() -> {
                Minecraft.getInstance().setScreen(null);
                importFiles(paths);
            });
        });
    }

    private void importFiles(List<Path> paths) {
        if (paths == null || paths.isEmpty()) {
            ChatUtil.sendMessage("You didnt choose a file!", ChatType.FAIL);
            return;
        }

        int valid = 0;
        files: for (var path : paths) {
            if (paths.size() != 1) {
                ChatUtil.sendMessage("Loading file: " + path.getFileName(), ChatType.INFO_BLUE);
            }
            Scanner sc;
            try {
                sc = new Scanner(path, StandardCharsets.UTF_8);
            } catch (IOException e) {
                ChatUtil.sendMessage("Failed to load file: " + path.getFileName(), ChatType.FAIL);
                continue;
            }

            List<String> lines = new ArrayList<>();

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.length() > 10000) {
                    ChatUtil.sendMessage("Line " + (lines.size() + 1) + " is too long! (" + line.length() + " > 10000)", ChatType.FAIL);
                    continue files;
                }
                lines.add(line);
                if (lines.size() > 10000) {
                    ChatUtil.sendMessage("File contains contains too many lines! (Max: 10,000)", ChatType.FAIL);
                    continue files;
                }
            }

            List<JsonObject> blocks = new ArrayList<>();
            List<String> current = new ArrayList<>();

            boolean first = true;
            for (String line : lines) {
                current.add(line);
                if (current.size() >= 26) {
                    blocks.add(block(current, first));
                    first = false;
                    current = new ArrayList<>();
                }
            }
            if (!current.isEmpty()) blocks.add(block(current, first));

            String template;
            try {
                template = template(blocks);
            } catch (IOException e) {
                ChatUtil.sendMessage("Failed to generate template for file: " + path.getFileName(), ChatType.FAIL);
                continue;
            }
            if (template.getBytes().length > 65536) { // i have no idea what the actual limit is it just seems to be close to this TODO: nice
                ChatUtil.sendMessage("Your file is too large!", ChatType.FAIL);
            } else {
                ItemStack item = new ItemStack(Items.ENDER_CHEST);
                TemplateUtil.applyRawTemplateNBT(item, path.getFileName().toString(), ModConstants.MOD_NAME, template);
                ItemUtil.giveCreativeItem(item, paths.size() == 1);
                if (paths.size() != 1) try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // temporary TODO: what
                    throw new RuntimeException(e);
                }
                valid++;
            }
        }
        if (paths.size() != 1 && valid > 0)
            ChatUtil.sendMessage("Loaded " + valid + " files!", ChatType.SUCCESS);
    }

    private String template(List<JsonObject> iblocks) throws IOException {
        JsonArray blocks = new JsonArray();
        blocks.add(JsonParser.parseString("{\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[]},\"data\":\"file\"}"));
        for (JsonObject block : iblocks) {
            blocks.add(block);
        }
        JsonObject root = new JsonObject();
        root.add("blocks", blocks);
        byte[] b64 = CompressionUtil.toBase64(CompressionUtil.toGZIP(root.toString().getBytes(StandardCharsets.UTF_8)));
        return new String(b64);
    }

    private JsonObject block(List<String> texts, boolean first) {
        JsonObject var = JsonParser.parseString("{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"file\",\"scope\":\"local\"}},\"slot\":0}").getAsJsonObject();
        JsonArray items = new JsonArray();
        items.add(var);
        for (String text : texts) {
            items.add(textItem(text, items.size()));
        }
        JsonObject args = new JsonObject();
        args.add("items", items);
        JsonObject root = new JsonObject();
        root.add("args", args);
        root.addProperty("id", "block");
        root.addProperty("block", "set_var");
        root.addProperty("action", first ? "CreateList" : "AppendValue");
        return root;
    }

    private JsonObject textItem(String text, int slot) {
        JsonObject data = new JsonObject();
        data.addProperty("name", text);
        JsonObject item = new JsonObject();
        item.addProperty("id", "txt");
        item.add("data", data);
        JsonObject root = new JsonObject();
        root.add("item", item);
        root.addProperty("slot", slot);
        return root;
    }

    @Override
    public String getDescription() {
        return """
                [blue]/importfile[reset]

                Import a text file as a code template.
                [red]Notice[reset]: Does NOT support line wrapping so if the code line is too long it will get cut off""";
    }

    @Override
    public String getName() {
        return "/importfile";
    }
}
