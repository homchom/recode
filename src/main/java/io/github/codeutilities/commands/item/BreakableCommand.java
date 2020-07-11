package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;

import io.github.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;

public class BreakableCommand {
	static MinecraftClient mc = MinecraftClient.getInstance();
	
	public static void run() {
		if (mc.player.isCreative()) {
			ItemStack item = mc.player.getMainHandStack();
			if (item.getItem() != Items.AIR) {
				CompoundTag nbt = item.getOrCreateTag();
				nbt.putBoolean("Unbreakable", false);
				item.setTag(nbt);
				mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
				CodeUtilities.chat("§aThe item you're holding is now breakable!");
			}else {
				CodeUtilities.chat("§cYou need to hold an item in your main hand!");
			}
		}else {
			CodeUtilities.chat("§cYou need to be in creative mode to use this command!");
		}
	}
	
	public static void register(CommandDispatcher<CottonClientCommandSource> cd) {
		cd.register(ArgumentBuilders.literal("breakable")
			.executes(ctx -> {
				try {
					run();
					return 1;
				}catch (Exception err) {
					CodeUtilities.chat("§cError while executing command.");
					err.printStackTrace();
					return -1;
				}
			})
		);
	}
}
