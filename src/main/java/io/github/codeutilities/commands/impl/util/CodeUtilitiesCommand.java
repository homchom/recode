package io.github.codeutilities.commands.impl.util;


import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.sys.Command;
import io.github.codeutilities.commands.sys.arguments.ArgBuilder;
import io.github.codeutilities.mixin.screen.gui.menus.codeutilities_menu.CodeUtilitiesUI;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class CodeUtilitiesCommand extends Command {
    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("codeutilities")
                .executes(ctx -> {
                    CodeUtilitiesUI gui = new CodeUtilitiesUI();
                    gui.scheduleOpenGui(gui);
                    return 1;
                })
        );
    }
}