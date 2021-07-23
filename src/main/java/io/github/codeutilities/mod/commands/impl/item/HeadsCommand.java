package io.github.codeutilities.mod.commands.impl.item;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.mod.features.commands.HeadsMenu;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class HeadsCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("heads").executes(ctx -> {
            if (!Config.getBoolean("headsEnabled")) {
                mc.player.sendChatMessage("/hypercube:heads");
                return 1;
            }
            if (this.isCreative(mc)) {
                HeadsMenu headMenu = HeadsMenu.getInstance();
                headMenu.scheduleOpenGui(headMenu, "");
            }
            return 1;
        }).then(ArgBuilder.argument("query", StringArgumentType.greedyString()).executes(ctx -> {
            if (!Config.getBoolean("headsEnabled")) {
                mc.player.sendChatMessage("/hypercube:heads " + ctx.getArgument("query", String.class));
                return 1;
            }
            if (this.isCreative(mc)) {
                String query = ctx.getArgument("query", String.class);
                HeadsMenu headMenu = HeadsMenu.getInstance();
                headMenu.scheduleOpenGui(headMenu, query);
            }
            return 1;
        })));
    }
}
