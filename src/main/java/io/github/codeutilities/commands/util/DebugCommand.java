package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.commands.arguments.LiteralArgument;
import io.github.codeutilities.util.chat.MessageGrabber;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class DebugCommand extends Command {
    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        if(CodeUtilities.BETA) {
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
