package io.github.codeutilities.mod.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.sys.commands.Command;
import io.github.codeutilities.sys.commands.arguments.ArgBuilder;
import io.github.codeutilities.sys.util.chat.ChatType;
import io.github.codeutilities.sys.util.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;

public class BreakableCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
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
