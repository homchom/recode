package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.util.ChatType;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class CopyColorCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("color")
                .then(ArgBuilder.argument("color", StringArgumentType.greedyString()).executes((context) -> {
                    String color = context.getArgument("color", String.class).replace("#", "");

                    MinecraftClient.getInstance().keyboard.setClipboard("&x&" + String.join("&", color.split("")));
                    CodeUtilities.chat("Copied Color!", ChatType.INFO_BLUE);
                    return 1;

                })));
    }
}
