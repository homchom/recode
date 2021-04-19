package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.gui.CustomHeadMenu;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class HeadsCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("heads").executes(ctx -> {
            if (this.isCreative(mc)) {
                CustomHeadMenu headMenu = CustomHeadMenu.getInstance();
                headMenu.open("");
                headMenu.scheduleOpenGui(headMenu);
            }
            return 1;
        }).then(ArgBuilder.argument("query", StringArgumentType.greedyString()).executes(ctx -> {
            if (this.isCreative(mc)) {
                String query = ctx.getArgument("query", String.class);
                CustomHeadMenu headMenu = CustomHeadMenu.getInstance();
                headMenu.open(query);
                headMenu.scheduleOpenGui(headMenu);
            }
            return 1;
        })));
    }
}
