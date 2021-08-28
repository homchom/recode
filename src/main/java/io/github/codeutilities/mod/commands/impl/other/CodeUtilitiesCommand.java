package io.github.codeutilities.mod.commands.impl.other;


import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.mod.features.commands.codeutilities.CodeUtilitiesUI;
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

    @Override
    public String getDescription() {
        return "[blue]/codeutilities[reset]\n\nShows information about this mod, such as this help menu, mod contributors, etc.";
    }

    @Override
    public String getName() {
        return "/codeutilities";
    }
}