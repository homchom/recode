package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.*;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.registry.Registry;

public class ItemdataCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("itemdata")
                .executes(ctx -> {
                    ItemStack item = mc.player.getMainHandStack();
                    CompoundTag nbt = item.getTag();
                    if (nbt != null) {
                        ChatUtil.sendMessage(String.format("§5----------§dItem Data for %s§5----------", item.getName().getString()));
                        mc.player.sendMessage(nbt.toText("  ", 0), false);


                        String formatted = nbt.toText("  ", 0).getString();
                        String unformatted = nbt.toString();

                        LiteralText msg1 = new LiteralText("§5Click here to copy a ");
                        LiteralText msg2 = new LiteralText("§d§lFormatted§5, ");
                        LiteralText msg3 = new LiteralText("§d§lUnformatted");
                        LiteralText msg4 = new LiteralText("§5 or ");
                        LiteralText msg5 = new LiteralText("§d§l/give");
                        LiteralText msg6 = new LiteralText("§5 version!");

                        msg2.styled((style) -> style.withClickEvent(new ClickEvent(Action.RUN_COMMAND, "/copytxt " + formatted)));
                        msg3.styled((style) -> style.withClickEvent(new ClickEvent(Action.RUN_COMMAND, "/copytxt " + formatted)));
                        msg5.styled((style) -> style.withClickEvent(new ClickEvent(Action.RUN_COMMAND, "/copytxt " + "/give " + Registry.ITEM.getId(item.getItem()).toString() + unformatted + " 1")));

                        mc.player.sendMessage(msg1.append(msg2).append(msg3).append(msg4).append(msg5).append(msg6), false);

                    } else {
                        ChatUtil.sendMessage("No NBT data found!", ChatType.FAIL);
                    }
                    return 1;
                })
        );
    }
}
