package io.github.codeutilities.mod.commands.other;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.menu.ConfigScreen;
import io.github.codeutilities.sys.commands.Command;
import io.github.codeutilities.sys.commands.arguments.ArgBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class ConfigCommand extends Command {
    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("cuconfig")
                .executes(ctx -> {
                    CodeUtilities.MC.openScreen(ConfigScreen.getScreen(CodeUtilities.MC.currentScreen));
                    return 1;
                })
        );
    }
}
