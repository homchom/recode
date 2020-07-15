package io.github.codeutilities.commands.impl.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.impl.Command;
import io.github.codeutilities.util.ChatType;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class CopyTextCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgumentBuilders.literal("copytxt")
                .then(ArgumentBuilders.argument("text", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            MinecraftClient.getInstance().keyboard.setClipboard(ctx.getArgument("text", String.class));
                            CodeUtilities.chat("Copied text!", ChatType.INFO_BLUE);
                            return 1;
                        })
                )
        );
    }
}
