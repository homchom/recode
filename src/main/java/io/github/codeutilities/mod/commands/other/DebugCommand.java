package io.github.codeutilities.mod.commands.other;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.sys.commands.Command;
import io.github.codeutilities.sys.commands.arguments.ArgBuilder;
import io.github.codeutilities.sys.util.chat.MessageGrabber;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class DebugCommand extends Command {
    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        if (CodeUtilities.BETA) {
            cd.register(ArgBuilder.literal("delayMessages")
                    .then(ArgBuilder.argument("messages", IntegerArgumentType.integer(0, 10))
                            .executes(ctx -> {
                                int messages = ctx.getArgument("messages", Integer.class);
                                mc.player.sendMessage(Text.of("[Debug] The next " + messages + " messages will be delayed."), false);
                                MessageGrabber.grabSilently(messages, msgs -> msgs.forEach(m -> mc.player.sendMessage(m, false)));
                                return 1;
                            })));
        }
    }
}
