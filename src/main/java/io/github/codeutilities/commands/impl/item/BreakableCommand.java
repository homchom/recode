package io.github.codeutilities.commands.impl.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.sys.Command;
import io.github.codeutilities.commands.sys.arguments.ArgBuilder;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;

public class BreakableCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("breakable")
                .executes(ctx -> {
                    if (this.isCreative(mc)) {
                        ItemStack item = mc.player.getMainHandStack();
                        if (item.getItem() != Items.AIR) {
                            CompoundTag nbt = item.getOrCreateTag();
                            nbt.putBoolean("Unbreakable", false);
                            item.setTag(nbt);
                            mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
                            ChatUtil.sendMessage("The item you're holding is now breakable!", ChatType.SUCCESS);
                        } else {
                            ChatUtil.sendMessage("You need to hold an item in your main hand!", ChatType.FAIL);
                        }
                    } else {
                        return -1;
                    }
                    return 1;
                })
        );
    }
}
