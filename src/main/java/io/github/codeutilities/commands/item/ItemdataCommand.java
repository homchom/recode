package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.util.ChatType;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.text.LiteralText;

public class ItemdataCommand {

    static MinecraftClient mc = MinecraftClient.getInstance();

    public static void run() {
        assert mc.player != null;
        ItemStack item = mc.player.getMainHandStack();
        if (item.getItem() != Items.AIR) {
            CompoundTag nbt = item.getTag();
            if (nbt != null) {
                mc.player.sendMessage(new LiteralText("§5----------§dItem Data for ").append(item.getName()).append(new LiteralText("§5----------")),false);
                mc.player.sendMessage(nbt.toText("  ",0), false);

                LiteralText msg1 = new LiteralText("§5Click here to copy a ");
                LiteralText msg2 = new LiteralText("§d§lFormatted");
                LiteralText msg3 = new LiteralText("§5 or ");
                LiteralText msg4 = new LiteralText("§d§lUnformatted");
                LiteralText msg5 = new LiteralText("§5 version!");

                String formatted = nbt.toText("  ", 0).getString();
                String unformatted = nbt.toString();

                msg2.styled((style)-> style.withClickEvent(new ClickEvent(Action.RUN_COMMAND, "/copytxt " + formatted)));
                msg4.styled((style)-> style.withClickEvent(new ClickEvent(Action.RUN_COMMAND, "/copytxt " + unformatted)));

                mc.player.sendMessage(msg1.append(msg2).append(msg3).append(msg4).append(msg5), false);


            } else CodeUtilities.chat("No NBT data found!", ChatType.FAIL);
        } else CodeUtilities.chat("You need to hold something!", ChatType.FAIL);
    }

    public static void register(CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgumentBuilders.literal("itemdata")
            .executes(ctx -> {
                if (!ModConfig.getConfig().improvedItemdataCmd) {
                    return 0;
                }
                try {
                    run();
                    return 1;
                } catch (Exception err) {
                    CodeUtilities.chat("Error while executing command.", ChatType.FAIL);
                    err.printStackTrace();
                    return -1;
                }
            })
        );
    }
}
