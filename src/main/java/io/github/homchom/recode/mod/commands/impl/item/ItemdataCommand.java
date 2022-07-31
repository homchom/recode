package io.github.homchom.recode.mod.commands.impl.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.*;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.world.item.ItemStack;

public class ItemdataCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        cd.register(ArgBuilder.literal("itemdata")
                .executes(ctx -> {
                    ItemStack item = mc.player.getMainHandItem();
                    CompoundTag nbt = item.getTag();
                    if (nbt != null) {
                        ChatUtil.sendMessage(String.format("§5----------§dItem Data for %s§5----------", item.getHoverName().getString()));
                        mc.player.displayClientMessage(Component.literal(nbt.toString()), false);


                        //String formatted = nbt.toText("  ", 0).getString();
                        String unformatted = nbt.toString();

                        MutableComponent msg1 = Component.literal("§5Click here to copy a ");
                        //TextComponent msg2 = new TextComponent("§d§lFormatted§5, ");
                        MutableComponent msg3 = Component.literal("§d§lUnformatted");
                        MutableComponent msg4 = Component.literal("§5 or ");
                        MutableComponent msg5 = Component.literal("§d§l/dfgive");
                        MutableComponent msg6 = Component.literal("§5 version!");

                        //msg2.withStyle((style) -> style.withClickEvent(new ClickEvent(Action.RUN_COMMAND, "/copytxt " + formatted)));
                        msg3.withStyle((style) -> style.withClickEvent(new ClickEvent(Action.RUN_COMMAND, "/copytxt " + unformatted)));
                        msg5.withStyle((style) -> style.withClickEvent(new ClickEvent(Action.RUN_COMMAND, "/copytxt " + "/dfgive " + Registry.ITEM.getKey(item.getItem()) + unformatted + " 1")));

                        this.sendMessage(mc, msg1/*.append(msg2)*/.append(msg3).append(msg4).append(msg5).append(msg6));

                    } else {
                        ChatUtil.sendMessage("No NBT data found!", ChatType.FAIL);
                    }
                    return 1;
                })
        );
    }

    @Override
    public String getDescription() {
        return "[blue]/itemdata[reset]\n"
                + "\n"
                + "Shows the item NBT of the item you are holding.";
    }

    @Override
    public String getName() {
        return "/itemdata";
    }
}
