package io.github.codeutilities.mod.commands.text;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.sys.commands.Command;
import io.github.codeutilities.sys.commands.arguments.ArgBuilder;
import io.github.codeutilities.sys.util.chat.ChatType;
import io.github.codeutilities.sys.util.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class CopyTextCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
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
