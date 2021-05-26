package io.github.codeutilities.commands.impl.util;


import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.sys.Command;
import io.github.codeutilities.commands.sys.arguments.ArgBuilder;
import io.github.codeutilities.util.gui.menus.codeutilities_menu.CodeUtilitiesUI;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class CodeUtilitiesCommand extends Command {
    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("codeutilities")
                .executes(ctx -> {
                    CodeUtilitiesUI gui = new CodeUtilitiesUI();
                    gui.scheduleOpenGui(gui);
                    return 1;
                })
        );
    }
}