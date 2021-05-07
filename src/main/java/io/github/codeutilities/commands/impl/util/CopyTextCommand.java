package io.github.codeutilities.commands.impl.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.commands.sys.Command;
import io.github.codeutilities.commands.sys.arguments.ArgBuilder;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class CopyTextCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("copytxt")
                .then(ArgBuilder.argument("text", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            mc.keyboard.setClipboard(ctx.getArgument("text", String.class));
                            ChatUtil.sendMessage("Copied text!", ChatType.INFO_BLUE);
                            return 1;
                        })
                )
        );
    }
}
