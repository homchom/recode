package io.github.codeutilities.mod.commands.impl.other;


import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.mod.features.commands.queue.QueueEntry;
import io.github.codeutilities.mod.features.commands.queue.QueueMenu;
import io.github.codeutilities.sys.networking.WebUtil;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueueCommand extends Command {
    private final String TWITCH_PLOT_QUEUE_URL = "https://twitch.center/customapi/quote/list?token=18a3878c";

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("queue")
                .executes(ctx -> {

                    String rawQueue;
                    try {
                        rawQueue = WebUtil.getString(TWITCH_PLOT_QUEUE_URL);
                    } catch (IOException e) {
                        ChatUtil.sendMessage("Error while requesting");
                        return 0;
                    }

                    String[] splitQueue = rawQueue.replaceFirst("\\n", "").split("\\n");
                    LinkedHashSet<QueueEntry> queue = new LinkedHashSet<>();

                    for (String entry : splitQueue) {
                        queue.add(
                                new QueueEntry(entry)
                        );
                    }

                    QueueMenu menu = new QueueMenu(queue);
                    menu.scheduleOpenGui(menu);
                    return 1;
                })
        );
    }
}