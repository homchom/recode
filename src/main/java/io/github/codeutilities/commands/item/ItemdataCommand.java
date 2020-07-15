package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.util.ChatType;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.text.LiteralText;

public class ItemdataCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgumentBuilders.literal("itemdata")
                .executes(ctx -> {
                    ItemStack item = mc.player.getMainHandStack();
                    CompoundTag nbt = item.getTag();
                    if (nbt != null) {
                        CodeUtilities.chat(String.format("§5----------§dItem Data for %s §5----------", item.getName().getString()));
                        mc.player.sendMessage(nbt.toText("  ", 0), false);


                        String formatted = nbt.toText("  ", 0).getString();
                        String unformatted = nbt.toString();

                        LiteralText msg1 = new LiteralText("§5Click here to copy a ");
                        LiteralText msg2 = new LiteralText("§d§lFormatted");
                        LiteralText msg3 = new LiteralText("§5 or ");
                        LiteralText msg4 = new LiteralText("§d§lUnformatted");
                        LiteralText msg5 = new LiteralText("§5 version!");


                        msg2.styled((style) -> style.withClickEvent(new ClickEvent(Action.RUN_COMMAND, "/copytxt " + formatted)));
                        msg4.styled((style) -> style.withClickEvent(new ClickEvent(Action.RUN_COMMAND, "/copytxt " + unformatted)));

                        mc.player.sendMessage(msg1.append(msg2).append(msg3).append(msg4).append(msg5), false);

                    } else {
                        CodeUtilities.chat("No NBT data found!", ChatType.FAIL);
                    }
                    return 1;
                })
        );
    }
}
