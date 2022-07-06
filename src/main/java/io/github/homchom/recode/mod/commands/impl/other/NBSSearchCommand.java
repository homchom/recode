package io.github.homchom.recode.mod.commands.impl.other;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.homchom.recode.LegacyRecode;
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
                    if (!LegacyRecode.MC.player.isCreative()) {
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
}
