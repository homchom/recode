package io.github.codeutilities.mod.commands.impl.other;

import static io.github.codeutilities.mod.commands.arguments.ArgBuilder.argument;
import static io.github.codeutilities.mod.commands.arguments.ArgBuilder.literal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.features.commands.nbs.NBSToTemplate;
import io.github.codeutilities.mod.features.commands.nbs.SongData;
import io.github.codeutilities.sys.hypercube.templates.TemplateUtils;
import io.github.codeutilities.sys.networking.WebUtil;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import io.github.codeutilities.sys.renderer.ToasterUtil;
import io.github.codeutilities.sys.util.ItemUtil;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.text.LiteralText;

public class NBSSearchCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(literal("nbssearch")
                    .then(argument("query",StringArgumentType.greedyString())
                        .executes(this::search)));
    }

    private int search(CommandContext<FabricClientCommandSource> ctx) {
        String query = ctx.getArgument("query",String.class);

        if (query.startsWith("load#")) {
            load(query);
            return 1;
        }

        ChatUtil.sendMessage("Searching " + query + "...",ChatType.INFO_BLUE);
        CodeUtilities.EXECUTOR.submit(() -> {
            try {
                JsonArray results = WebUtil.getJson("https://untitled-57qvszfgg28u.runkit.sh/search?query="+
                    URLEncoder.encode(query, StandardCharsets.UTF_8)).getAsJsonArray();

                for (JsonElement e : results) {
                    JsonObject result = e.getAsJsonObject();

                    LiteralText txt = new LiteralText("§b- " + result.get("title").getAsString() + " " + result.get("duration").getAsString());

                    txt.styled((style) -> style.withClickEvent(new ClickEvent(Action.RUN_COMMAND,"/nbssearch load#" + result.get("id").getAsString())));

                    this.sendMessage(CodeUtilities.MC,txt);
                }

                ChatUtil.sendMessage("Results for " + query,ChatType.INFO_YELLOW);


            } catch (Exception err) {
                err.printStackTrace();
                ChatUtil.sendMessage("Error while executing command", ChatType.FAIL);
            }
        });
        return 1;
    }

    private void load(String query) {
        CodeUtilities.EXECUTOR.submit(() -> {
            try {
                String id = query.split("#")[1];
                ChatUtil.sendMessage("Importing... (ID: " + id + ")",ChatType.INFO_BLUE);

                String notes = WebUtil.getString("https://untitled-57qvszfgg28u.runkit.sh/download?id=" + id + "&format=mcnbs");

                System.out.println(notes);

                String[] notearr = notes.split("=");

                int length = Integer.parseInt(notearr[notearr.length-1].split(":")[0]);

                SongData d = new SongData("ImportedSong", "CodeUtilities", 20f, length, notes, "", "", 1, 0, 0);

                String code = new NBSToTemplate(d).convert();
                ItemStack stack = new ItemStack(Items.NOTE_BLOCK);
                TemplateUtils.compressTemplateNBT(stack, d.getName(), d.getAuthor(), code);

                stack.setCustomName(new LiteralText("§bImportedSong"));

                ItemUtil.giveCreativeItem(stack, true);

            } catch (Exception err) {
                err.printStackTrace();
                ChatUtil.sendMessage("Error while executing command", ChatType.FAIL);
            }
        });
    }
}
