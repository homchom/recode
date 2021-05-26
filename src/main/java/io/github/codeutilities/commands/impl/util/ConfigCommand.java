package io.github.codeutilities.commands.impl.util;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.sys.Command;
import io.github.codeutilities.commands.sys.arguments.ArgBuilder;
import io.github.codeutilities.config.menu.ConfigScreen;
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
