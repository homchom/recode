package io.github.codeutilities.commands.impl.util;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.sys.Command;
import io.github.codeutilities.commands.sys.arguments.ArgBuilder;
import io.github.codeutilities.config.CodeUtilsConfig;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

// Broken

public class ConfigCommand extends Command {
    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("cuconfig")
                .executes(ctx -> {
                    CodeUtilities.EXECUTOR.submit(() -> {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        MinecraftClient.getInstance().openScreen(CodeUtilsConfig.getScreen(MinecraftClient.getInstance().currentScreen));
                    });
                    return 1;
                })
        );
    }
}
