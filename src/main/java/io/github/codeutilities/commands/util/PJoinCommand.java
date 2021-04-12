package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.commands.arguments.types.PlayerArgumentType;
import io.github.codeutilities.events.ChatReceivedEvent;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class PJoinCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("pjoin")
                .then(ArgBuilder.argument("player", PlayerArgumentType.player())
                        .executes(ctx -> {
                            try {
                                return run(mc, ctx.getArgument("player", String.class));
                            }catch (Exception e) {
                                ChatUtil.sendMessage("Error while attempting to execute the command.", ChatType.FAIL);
                                e.printStackTrace();
                                return -1;
                            }
                        })
                )
        );
    }

    private int run(MinecraftClient mc, String player) {

        if (player == mc.player.getName().asString()) {
            ChatUtil.sendMessage("You cannot use this command on yourself!", ChatType.FAIL);
            return -1;
        }

        mc.player.sendChatMessage("/locate " + player);

        ChatReceivedEvent.pjoin = true;

        ChatUtil.sendMessage("Joining the plot §e" + player + "§b is currently playing...", ChatType.INFO_BLUE);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (ChatReceivedEvent.pjoin) {
                ChatUtil.sendMessage("Timeout error while trying to join the plot.", ChatType.FAIL);
            }
            ChatReceivedEvent.pjoin = false;
        }).start();
        return 1;
    }
}
