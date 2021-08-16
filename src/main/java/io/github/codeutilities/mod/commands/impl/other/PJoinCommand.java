package io.github.codeutilities.mod.commands.impl.other;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.mod.commands.arguments.types.PlayerArgumentType;
import io.github.codeutilities.mod.events.impl.ReceiveChatMessageEvent;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class PJoinCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("pjoin")
                .then(ArgBuilder.argument("player", PlayerArgumentType.player())
                        .executes(ctx -> {
                            try {
                                return run(mc, ctx.getArgument("player", String.class));
                            } catch (Exception e) {
                                ChatUtil.sendMessage("Error while attempting to execute the command.", ChatType.FAIL);
                                e.printStackTrace();
                                return -1;
                            }
                        })
                )
        );
    }

    @Override
    public String getDescription() {
        return "TODO: Add description";
    }

    @Override
    public String getName() {
        return "/pjoin";
    }

    private int run(MinecraftClient mc, String player) {

        if (player.equals(mc.player.getName().asString())) {
            ChatUtil.sendMessage("You cannot use this command on yourself!", ChatType.FAIL);
            return -1;
        }

        mc.player.sendChatMessage("/locate " + player);

        ReceiveChatMessageEvent.pjoin = true;
        ChatUtil.sendMessage("Joining the plot §e" + player + "§b is currently playing...", ChatType.INFO_BLUE);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (ReceiveChatMessageEvent.pjoin) {
                ChatUtil.sendMessage("Timeout error while trying to join the plot.", ChatType.FAIL);
            }
            ReceiveChatMessageEvent.pjoin = false;
        }).start();
        return 1;
    }
}
