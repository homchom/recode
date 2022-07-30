package io.github.homchom.recode.mod.commands.impl.other;

import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.commands.arguments.types.PlayerArgumentType;
import io.github.homchom.recode.mod.events.impl.LegacyReceiveChatMessageEvent;
import io.github.homchom.recode.sys.player.chat.*;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;

public class PJoinCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
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
        return "[blue]/pjoin <player>[reset]\n"
                + "\n"
                + "Join the plot the specified player is currently playing.";
    }

    @Override
    public String getName() {
        return "/pjoin";
    }

    private int run(Minecraft mc, String player) {

        if (player.equals(mc.player.getName().getString())) {
            ChatUtil.sendMessage("You cannot use this command on yourself!", ChatType.FAIL);
            return -1;
        }

        mc.player.commandSigned("/locate " + player, null);

        LegacyReceiveChatMessageEvent.pjoin = true;
        ChatUtil.sendMessage("Joining the plot §e" + player + "§b is currently playing...", ChatType.INFO_BLUE);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (LegacyReceiveChatMessageEvent.pjoin) {
                ChatUtil.sendMessage("Timeout error while trying to join the plot.", ChatType.FAIL);
            }
            LegacyReceiveChatMessageEvent.pjoin = false;
        }).start();
        return 1;
    }
}
