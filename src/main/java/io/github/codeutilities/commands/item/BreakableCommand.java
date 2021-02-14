package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
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
                    if (mc.player.isCreative()) {
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
                        ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
                    }
                    return 1;
                })
        );
    }
}
