package io.github.codeutilities.mod.commands.impl.other;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.mod.commands.arguments.types.FreeStringArgumentType;
import io.github.codeutilities.mod.features.commands.queue.QueueEntry;
import io.github.codeutilities.mod.features.social.tab.CodeUtilitiesServer;
import io.github.codeutilities.mod.features.social.tab.WebMessage;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;

import java.util.LinkedHashSet;

public class QueueCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        LiteralArgumentBuilder<FabricClientCommandSource> cmd = ArgBuilder.literal("queue")
                .executes(ctx -> {

                    try {
                        CodeUtilitiesServer.requestMessage(new WebMessage("twitch-queue"), message -> {

                            String[] splitQueue = message.getContent().getAsString().split("\\n");
                            LinkedHashSet<QueueEntry> queue = new LinkedHashSet<>();

                            int i = 0;
                            for (String entry : splitQueue) {
                                QueueEntry queueEntry = new QueueEntry(entry, i);
                                if(!QueueEntry.HIDDEN_ENTRIES.contains(queueEntry.getPlotId()==null?"?":queueEntry.getPlotId().toString())){
                                    i++;
                                    queue.add(
                                            new QueueEntry(entry, i)
                                    );
                                }
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
                                MutableText entrymsg = new LiteralText("#" + entry.getPosition())
                                        .styled(style -> style.withColor(TextColor.fromRgb(0x00bbff)
                                                ).withClickEvent(
                                                new ClickEvent(
                                                        ClickEvent.Action.RUN_COMMAND,
                                                        "/queue hideandjoin "+entry.getPlotId()
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
                                                        ))));
                                if(entry.isBeta()) {
                                    entrymsg.append(
                                            new LiteralText("\n§r §r §r §r §r §r §r §r §r §r §r §r ↑ ")
                                            .styled(style -> style.withColor(TextColor.fromRgb(0x7a2626))).append(
                                                    new LiteralText("This plot may be on ")
                                                            .styled(style -> style.withColor(TextColor.fromRgb(0xc96363))).append(
                                                            new LiteralText("Node Beta")
                                                                    .styled(style -> style.withColor(TextColor.fromRgb(0xd95104))).append(
                                                                    new LiteralText(" ↑")
                                                                            .styled(style -> style.withColor(TextColor.fromRgb(0x7a2626)))
                                                            ))));
                                }
                                ChatUtil.sendMessage(entrymsg, null);
                            }

                            ChatUtil.sendMessage("");
                        });
                    } catch (Exception e) {
                        ChatUtil.sendMessage("Error while requesting");
                        e.printStackTrace();
                        return 0;
                    }

                    return 1;
                });
                cmd.then(ArgBuilder.argument("type", FreeStringArgumentType.string())
                        .then(ArgBuilder.argument("id", FreeStringArgumentType.string())
                                .executes(ctx -> {
                                    String id = ctx.getArgument("id", String.class);
                                    String type = ctx.getArgument("type", String.class);

                                    if(!id.equals("null")) {

                                        if(type.equals("hideandjoin")) {

                                            QueueEntry.HIDDEN_ENTRIES.add(id);

                                            mc.player.sendChatMessage("/join " + id);

                                            mc.player.sendMessage(new LiteralText("⏩ ")
                                                    .styled(style -> style.withColor(TextColor.fromRgb(0x34961d))
                                                            .withClickEvent(
                                                                    new ClickEvent(
                                                                            ClickEvent.Action.RUN_COMMAND,
                                                                            "/queue show "+id
                                                                    ))
                                                            .withHoverEvent(
                                                                    new HoverEvent(
                                                                            HoverEvent.Action.SHOW_TEXT,
                                                                            new LiteralText("§7Click to unhide!")
                                                                    )
                                                            )).append(
                                                            new LiteralText("Plot " + id + " hidden from queue. Click here to unhide!")
                                                                    .styled(style -> style.withColor(TextColor.fromRgb(0xb3ffa1))).append(
                                                                    new LiteralText(" ⏪")
                                                                            .styled(style -> style.withColor(TextColor.fromRgb(0x34961d)))
                                                            )), false);
                                        }

                                        if(type.equals("show")) {
                                            QueueEntry.HIDDEN_ENTRIES.remove(id);

                                            mc.player.sendMessage(new LiteralText("⏩ ")
                                                    .styled(style -> style.withColor(TextColor.fromRgb(0x37a61c))).append(
                                                            new LiteralText("Plot " + id + " will now be shown in queue.")
                                                                    .styled(style -> style.withColor(TextColor.fromRgb(0xb3ffa1))).append(
                                                                    new LiteralText(" ⏪")
                                                                            .styled(style -> style.withColor(TextColor.fromRgb(0x37a61c)))
                                                            )), false);
                                        }

                                    } else {
                                        mc.player.sendMessage(new LiteralText("⏩ ")
                                                .styled(style -> style.withColor(TextColor.fromRgb(0x961d1d))).append(
                                                        new LiteralText("Invalid plot ID!")
                                                                .styled(style -> style.withColor(TextColor.fromRgb(0xffa1a1))).append(
                                                                new LiteralText(" ⏪")
                                                                        .styled(style -> style.withColor(TextColor.fromRgb(0x961d1d)))
                                                        )), false);
                                    }

                                    return 1;
                                })
                        ));
        cd.register(cmd);
    }

    @Override
    public String getDescription() {
        return "TODO: Add description";
    }

    @Override
    public String getName() {
        return "/queue";
    }
}