package io.github.codeutilities.mod.commands.impl.other;


import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.mod.features.commands.codeutilities.CodeUtilitiesUI;
import io.github.codeutilities.sys.networking.WebRequester;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;

public class QueueCommand extends Command {
    private final String TWITCH_PLOT_QUEUE_URL = "https://twitch.center/customapi/quote/list?token=18a3878c";

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("queue")
                .executes(ctx -> {
                    String rawQueue;
                    try {
                        rawQueue = WebRequester.getString(TWITCH_PLOT_QUEUE_URL);
                    } catch (IOException e) {
                        ChatUtil.sendMessage("Error while requesting");
                        return 0;
                    }

                    System.out.println(rawQueue);

                    /*
                    CodeUtilitiesUI gui = new CodeUtilitiesUI();
                    gui.scheduleOpenGui(gui);

                     */
                    return 1;
                })
        );
    }
}