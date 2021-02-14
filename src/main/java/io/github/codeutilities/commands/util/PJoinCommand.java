package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.events.ChatReceivedEvent;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.codeutilities.util.DFInfo;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class PJoinCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("pjoin")
                .then(ArgBuilder.argument("player", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            try {
                                return run(mc, StringArgumentType.getString(ctx, "player"));
                            } catch (Exception e) {
                                ChatUtil.sendMessage("Error while attempting to execute the command.", ChatType.FAIL);
                                e.printStackTrace();
                                return -1;
                            }
                        })
                )
        );
    }

    private int run(MinecraftClient mc, String player) {
        if (DFInfo.currentState != DFInfo.State.LOBBY) {
            mc.player.sendChatMessage("/leave");
        }
        mc.player.sendChatMessage("/locate " + player);

        ChatReceivedEvent.pjoin = true;

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
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
