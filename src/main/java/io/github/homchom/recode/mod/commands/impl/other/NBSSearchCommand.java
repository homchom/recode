package io.github.homchom.recode.mod.commands.impl.other;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.features.commands.NbsSearchMenu;
import io.github.homchom.recode.sys.player.chat.*;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;

import static io.github.homchom.recode.mod.commands.arguments.ArgBuilder.argument;
import static io.github.homchom.recode.mod.commands.arguments.ArgBuilder.literal;

public class NBSSearchCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(literal("nbssearch")
            .then(argument("query", StringArgumentType.greedyString())
                .executes(ctx -> {
                    if (!Recode.MC.player.isCreative()) {
                        ChatUtil.sendMessage("You need to be in creative mode for this command to work!", ChatType.FAIL);
                        return -1;
                    }
                    NbsSearchMenu gui = new NbsSearchMenu(ctx.getArgument("query",String.class));
                    gui.scheduleOpenGui(gui);
                    return 1;
                })
            )
        );
    }

    @Override
    public String getDescription() {
        return "[blue]/nbssearch <query>[reset]\n"
                + "\n"
                + "Browses songs in musescore.com and converts them into code templates."
                + "Play the song with [green]Song Player[reset]. (see [yellow]/nbs[reset] command help for more info)";
    }

    @Override
    public String getName() {
        return "/nbssearch";
    }

    // Text based version below
//    @Override
//    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
//        cd.register(literal("nbssearch")
//                    .then(argument("query",StringArgumentType.greedyString())
//                        .executes(this::search)));
//    }
//
//    private int search(CommandContext<FabricClientCommandSource> ctx) {
//        String query = ctx.getArgument("query",String.class);
//
//        if (query.startsWith("load#")) {
//            load(query);
//            return 1;
//        }
//
//        ChatUtil.sendMessage("Searching " + query + "...",ChatType.INFO_BLUE);
//        Recode.EXECUTOR.submit(() -> {
//            try {
//                String sresults = RecodeServer.requestURL("https://untitled-57qvszfgg28u.runkit.sh/search?query="+
//                    URLEncoder.encode(query, StandardCharsets.UTF_8));
//
//                System.out.println(sresults);
//
//                JsonArray results = Recode.JSON_PARSER.parse(sresults).getAsJsonArray();
//
//                ChatUtil.sendMessage("Results for " + query,ChatType.INFO_YELLOW);
//                for (JsonElement e : results) {
//                    JsonObject result = e.getAsJsonObject();
//
//                    LiteralText txt = new LiteralText("§b- " + result.get("title").getAsString() + " " + result.get("duration").getAsString());
//
//                    txt.styled((style) -> style.withClickEvent(new ClickEvent(Action.RUN_COMMAND,"/nbssearch load#" + result.get("id").getAsString())));
//
//                    this.sendMessage(Recode.MC,txt);
//                }
//
//
//
//            } catch (Exception err) {
//                err.printStackTrace();
//                ChatUtil.sendMessage("Error while executing command", ChatType.FAIL);
//            }
//        });
//        return 1;
//    }
//
//    private void load(String query) {
//
//        if (!Recode.MC.player.isCreative()) {
//            ChatUtil.sendMessage("You need to be in creative to import songs!",ChatType.FAIL);
//            return;
//        }
//
//        Recode.EXECUTOR.submit(() -> {
//            try {
//                String id = query.split("#")[1];
//                ChatUtil.sendMessage("Importing... (ID: " + id + ")",ChatType.INFO_BLUE);
//
//                String notes = RecodeServer.requestURL("https://untitled-57qvszfgg28u.runkit.sh/download?id=" + id + "&format=mcnbs");
//
//                String[] notearr = notes.split("=");
//
//                int length = Integer.parseInt(notearr[notearr.length-1].split(":")[0]);
//
//                SongData d = new SongData("ImportedSong", "CodeUtilities", 20f, length, notes, "", "", 1, 0, 0);
//
//                String code = new NBSToTemplate(d).convert();
//                ItemStack stack = new ItemStack(Items.NOTE_BLOCK);
//                TemplateUtils.compressTemplateNBT(stack, d.getName(), d.getAuthor(), code);
//
//                stack.setCustomName(new LiteralText("§bImportedSong"));
//
//                ItemUtil.giveCreativeItem(stack, true);
//
//            } catch (Exception err) {
//                err.printStackTrace();
//                ChatUtil.sendMessage("Error while executing command", ChatType.FAIL);
//            }
//        });
//    }
}
