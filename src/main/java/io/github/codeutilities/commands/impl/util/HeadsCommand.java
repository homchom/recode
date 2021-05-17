package io.github.codeutilities.commands.impl.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.commands.sys.Command;
import io.github.codeutilities.commands.sys.arguments.ArgBuilder;
import io.github.codeutilities.config.Config;
import io.github.codeutilities.util.gui.menus.CustomHeadMenu;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class HeadsCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("heads").executes(ctx -> {
            if (!Config.getBoolean("headsEnabled")) {
                mc.player.sendChatMessage("/hypercube:heads");
                return 1;
            }
            if (this.isCreative(mc)) {
                CustomHeadMenu headMenu = CustomHeadMenu.getInstance();
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
                CustomHeadMenu headMenu = CustomHeadMenu.getInstance();
                headMenu.scheduleOpenGui(headMenu, query);
            }
            return 1;
        })));
    }
}
