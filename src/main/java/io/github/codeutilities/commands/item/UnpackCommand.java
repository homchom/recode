package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.ChatType;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;

public class UnpackCommand {
	static MinecraftClient mc = MinecraftClient.getInstance();
	
	public static void run() {
		if (mc.player.isCreative()) {
			ItemStack handItem = mc.player.getMainHandStack();
			if (handItem.getItem() != Items.AIR) {
				if (!mc.player.getMainHandStack().getOrCreateTag()
			            .getCompound("BlockEntityTag").isEmpty()) {
					ListTag nbt = mc.player.getMainHandStack().getOrCreateTag()
				            .getCompound("BlockEntityTag").getList("Items", 10);
				        int items = 0;
				        for (int i = 0; i < nbt.size(); i++) {
				          ItemStack item = ItemStack.fromTag(nbt.getCompound(i));
				          if (item.getItem() != Items.AIR) {
				            items++;
				          }
				          CodeUtilities.giveCreativeItem(item);
				        }
				        if (items == 0) {
				          CodeUtilities.chat("There are no items stored in this container!", ChatType.FAIL);
				        } else {
				          if (items == 1) CodeUtilities.chat("Unpacked §b" + items + "§a item!");
				          else CodeUtilities.chat("§aUnpacked §b" + items + "§a items!", ChatType.SUCCESS);
				        }
				}else {
					CodeUtilities.chat("Cannot find any items inside! :(", ChatType.FAIL);
				}
			}else {
				CodeUtilities.chat("You need to hold an item in your hand!", ChatType.FAIL);
			}
		}else {
			CodeUtilities.chat("You need to be in creative mode to use this command!", ChatType.FAIL);
		}
	}
	
	public static void register(CommandDispatcher<CottonClientCommandSource> cd) {
		cd.register(ArgumentBuilders.literal("unpack")
			.executes(ctx -> {
				try {
					run();
					return 1;
				}catch (Exception err) {
					CodeUtilities.chat("Error while executing command.", ChatType.FAIL);
					err.printStackTrace();
					return -1;
				}
			})
		);
	}
}
