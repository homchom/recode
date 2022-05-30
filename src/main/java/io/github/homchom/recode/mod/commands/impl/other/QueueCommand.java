package io.github.homchom.recode.mod.commands.impl.other;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.features.commands.queue.QueueEntry;
import io.github.homchom.recode.sys.networking.WebUtil;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import net.minecraft.sounds.SoundEvents;

import java.util.LinkedHashSet;

public class QueueCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd) {
        LiteralArgumentBuilder<FabricClientCommandSource> cmd = ArgBuilder.literal("queue")
                .executes(ctx -> {

                    try {
                        String content = WebUtil.getString("https://twitch.center/customapi/quote/list?token=18a3878c");

                        String[] splitQueue = content.split("\\n");
                        LinkedHashSet<QueueEntry> queue = new LinkedHashSet<>();

                        int i = 0;
                        for (String entry : splitQueue) {
                            QueueEntry queueEntry = new QueueEntry(entry, i);
                            if (!QueueEntry.HIDDEN_ENTRIES.contains(queueEntry.getPlotId()==null?"?":queueEntry.getPlotId().toString())){
                                i++;
                                queue.add(
                                        new QueueEntry(entry, i)
                                );
                            }
                        }

                        Recode.MC.player.playSound(SoundEvents.UI_TOAST_IN, 2F, 1F);

                        // Temporary: Show in chat instead of menu
                        ChatUtil.sendMessage(
                                new TextComponent("\n§r §r §r §r §r §r §r §r §r §r §r §r ").append(
                                        new TextComponent("⏪  ")
                                                .withStyle(style -> style.withColor(TextColor.fromRgb(0x1f9947))).append(
                                                new TextComponent("Recode Twitch Plot Queue  ")
                                                        .withStyle(style -> style.withColor(TextColor.fromRgb(0x33ffa7))).append(
                                                        new TextComponent("⏩")
                                                                .withStyle(style -> style.withColor(TextColor.fromRgb(0x1f9947)))
                                                ))), null);

                        for (QueueEntry entry : queue) {
                            MutableComponent entrymsg = new TextComponent("#" + entry.getPosition())
                                    .withStyle(style -> style.withColor(TextColor.fromRgb(0x00bbff)
                                            ).withClickEvent(
                                            new ClickEvent(
                                                    ClickEvent.Action.RUN_COMMAND,
                                                    "/queue hideandjoin "+entry.getPlotId()
                                            ))
                                                    .withHoverEvent(
                                                            new HoverEvent(
                                                                    HoverEvent.Action.SHOW_TEXT,
                                                                    new TextComponent("§7Click to join!")
                                                            )
                                                    )
                                    ).append(
                                    new TextComponent("§8 - ").append(
                                            new TextComponent(entry.getPlotId()==null?"?":entry.getPlotId().toString())
                                                    .withStyle(style -> style.withColor(TextColor.fromRgb(0x66e6ff))).append(
                                                    new TextComponent("§8 - ").append(
                                                            new TextComponent(entry.getStrippedDescription())
                                                                    .withStyle(style -> style.withColor(TextColor.fromRgb(0xbff9ff)))
                                                    ))));
                            if (entry.isBeta()) {
                                entrymsg.append(
                                        new TextComponent("\n§r §r §r §r §r §r §r §r §r §r §r §r ↑ ")
                                        .withStyle(style -> style.withColor(TextColor.fromRgb(0x7a2626))).append(
                                                new TextComponent("This plot may be on ")
                                                        .withStyle(style -> style.withColor(TextColor.fromRgb(0xc96363))).append(
                                                        new TextComponent("Node Beta")
                                                                .withStyle(style -> style.withColor(TextColor.fromRgb(0xd95104))).append(
                                                                new TextComponent(" ↑")
                                                                        .withStyle(style -> style.withColor(TextColor.fromRgb(0x7a2626)))
                                                        ))));
                            }
                            ChatUtil.sendMessage(entrymsg, null);
                        }

                        ChatUtil.sendMessage("");
                    } catch (Exception e) {
                        ChatUtil.sendMessage("Error while requesting");
                        e.printStackTrace();
                        return 0;
                    }

                    return 1;
                });
                cmd.then(ArgBuilder.argument("type", StringArgumentType.word())
                        .then(ArgBuilder.argument("id", StringArgumentType.word())
                                .executes(ctx -> {
                                    String id = ctx.getArgument("id", String.class);
                                    String type = ctx.getArgument("type", String.class);

                                    if (!id.equals("null")) {

                                        if (type.equals("hideandjoin")) {

                                            QueueEntry.HIDDEN_ENTRIES.add(id);

                                            mc.player.chat("/join " + id);

                                            mc.player.displayClientMessage(new TextComponent("⏩ ")
                                                    .withStyle(style -> style.withColor(TextColor.fromRgb(0x34961d))
                                                            .withClickEvent(
                                                                    new ClickEvent(
                                                                            ClickEvent.Action.RUN_COMMAND,
                                                                            "/queue show "+id
                                                                    ))
                                                            .withHoverEvent(
                                                                    new HoverEvent(
                                                                            HoverEvent.Action.SHOW_TEXT,
                                                                            new TextComponent("§7Click to unhide!")
                                                                    )
                                                            )).append(
                                                            new TextComponent("Plot " + id + " hidden from queue. Click here to unhide!")
                                                                    .withStyle(style -> style.withColor(TextColor.fromRgb(0xb3ffa1))).append(
                                                                    new TextComponent(" ⏪")
                                                                            .withStyle(style -> style.withColor(TextColor.fromRgb(0x34961d)))
                                                            )), false);
                                        }

                                        if (type.equals("show")) {
                                            QueueEntry.HIDDEN_ENTRIES.remove(id);

                                            mc.player.displayClientMessage(new TextComponent("⏩ ")
                                                    .withStyle(style -> style.withColor(TextColor.fromRgb(0x37a61c))).append(
                                                            new TextComponent("Plot " + id + " will now be shown in queue.")
                                                                    .withStyle(style -> style.withColor(TextColor.fromRgb(0xb3ffa1))).append(
                                                                    new TextComponent(" ⏪")
                                                                            .withStyle(style -> style.withColor(TextColor.fromRgb(0x37a61c)))
                                                            )), false);
                                        }

                                    } else {
                                        mc.player.displayClientMessage(new TextComponent("⏩ ")
                                                .withStyle(style -> style.withColor(TextColor.fromRgb(0x961d1d))).append(
                                                        new TextComponent("Invalid plot ID!")
                                                                .withStyle(style -> style.withColor(TextColor.fromRgb(0xffa1a1))).append(
                                                                new TextComponent(" ⏪")
                                                                        .withStyle(style -> style.withColor(TextColor.fromRgb(0x961d1d)))
                                                        )), false);
                                    }

                                    return 1;
                                })
                        ));
        cd.register(cmd);
    }

    @Override
    public String getDescription() {
        return "[blue]/queue[reset]\n"
                + "\n"
                + "Checks the Plot Queue of Jeremaster's DiamondFire stream.";
    }

    @Override
    public String getName() {
        return "/queue";
    }
}