package io.github.codeutilities.mod.commands.other;


import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.sys.commands.Command;
import io.github.codeutilities.sys.commands.arguments.ArgBuilder;
import io.github.codeutilities.sys.util.gui.menus.codeutilities_menu.CodeUtilitiesUI;
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