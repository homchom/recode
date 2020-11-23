package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.events.ChatReceivedEvent;
import io.github.codeutilities.gui.ItemEditorGui;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.codeutilities.util.DFInfo;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class RejoinCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("rejoin")
                .executes(ctx -> {
                    try {
                        return run(mc);
                    }catch (Exception e) {
                        ChatUtil.sendMessage("Error while attempting to execute the command.", ChatType.FAIL);
                        e.printStackTrace();
                        return -1;
                    }
                })
        );
    }

    private int run(MinecraftClient mc) {
        if (DFInfo.currentState != DFInfo.State.PLAY) {
            ChatUtil.sendMessage("You need to be in play mode to use this command!", ChatType.FAIL);
            return -1;
        }
        mc.player.sendChatMessage("/locate");
        mc.player.sendChatMessage("/spawn");

        ChatReceivedEvent.rejoinStep = 2;

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (ChatReceivedEvent.rejoinStep > 0) {
                ChatUtil.sendMessage("Timeout error while trying to rejoin the plot.", ChatType.FAIL);
            }
            ChatReceivedEvent.rejoinStep = 0;
        }).start();
        return 1;
    }
}
