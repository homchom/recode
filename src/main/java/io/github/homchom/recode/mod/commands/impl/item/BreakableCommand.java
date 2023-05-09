package io.github.homchom.recode.mod.commands.impl.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BreakableCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        cd.register(ArgBuilder.literal("breakable")
                .executes(ctx -> {
                    if (this.isCreative(mc)) {
                        ItemStack item = mc.player.getMainHandItem();
                        if (item.getItem() != Items.AIR) {
                            CompoundTag nbt = item.getOrCreateTag();
                            nbt.putBoolean("Unbreakable", false);
                            item.setTag(nbt);
                            mc.gameMode.handleCreativeModeItemAdd(item, 36 + mc.player.getInventory().selected);
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

    @Override
    public String getDescription() {
        return "[blue]/breakable[reset]\n"
                + "\n"
                + "Opposite of /unbreakable - Removes the Unbreakable tag from the item you are holding.";
    }

    @Override
    public String getName() {
        return "/breakable";
    }
}
