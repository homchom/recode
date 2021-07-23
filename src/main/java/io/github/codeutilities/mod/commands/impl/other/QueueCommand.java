package io.github.codeutilities.mod.commands.impl.other;


import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.mod.features.commands.queue.QueueEntry;
import io.github.codeutilities.mod.features.commands.queue.QueueMenu;
import io.github.codeutilities.sys.networking.WebUtil;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TextColor;

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

                    CodeUtilities.MC.player.playSound(SoundEvents.UI_TOAST_IN, 2F, 1F);

                    // Temporary: Show in chat instead of menu
                    ChatUtil.sendMessage(
                            new LiteralText("\n§r §r §r §r §r §r §r §r §r §r §r §r ").append(
                            new LiteralText("⏪  ")
                                    .styled(style -> style.withColor(TextColor.fromRgb(0x1f9947))).append(
                            new LiteralText("CodeUtilities Twitch Plot Queue  ")
                                    .styled(style -> style.withColor(TextColor.fromRgb(0x33ffa7))).append(
                            new LiteralText("⏩")
                                    .styled(style -> style.withColor(TextColor.fromRgb(0x1f9947)))
                    ))), null);

                    for (QueueEntry entry : queue) {
                        ChatUtil.sendMessage(
                                new LiteralText("#" + entry.getPosition())
                                        .styled(style -> style.withColor(TextColor.fromRgb(0x00bbff)
                                        ).withClickEvent(
                                                new ClickEvent(
                                                        ClickEvent.Action.RUN_COMMAND,
                                                        "/join "+entry.getPlotId()
                                                ))
                                                .withHoverEvent(
                                                        new HoverEvent(
                                                                HoverEvent.Action.SHOW_TEXT,
                                                                new LiteralText("§7Click to join!")
                                                        )
                                                )
                                        ).append(
                                new LiteralText("§8 - ").append(
                                new LiteralText(entry.getPlotId()==null?"?":entry.getPlotId().toString())
                                        .styled(style -> style.withColor(TextColor.fromRgb(0x66e6ff))).append(
                                new LiteralText("§8 - ").append(
                                new LiteralText(entry.getStrippedDescription())
                                        .styled(style -> style.withColor(TextColor.fromRgb(0xbff9ff)))
                        )))), null);
                    }

                    ChatUtil.sendMessage("");
                    return 1;

/*
                    QueueMenu menu = new QueueMenu(queue);
                    menu.scheduleOpenGui(menu);
                    return 1;

 */
                })
        );
    }
}