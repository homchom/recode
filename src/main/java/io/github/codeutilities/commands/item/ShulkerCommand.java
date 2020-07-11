package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;

import io.github.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;

public class ShulkerCommand {
	static MinecraftClient mc = MinecraftClient.getInstance();
	
	public static void run() {
		if (mc.player.isCreative()) {
			ListTag nbt = mc.player.getMainHandStack().getOrCreateTag()
		            .getCompound("BlockEntityTag").getList("Items", 10);
		        int items = 0;
		        for (int i = 0; i < nbt.size(); i++) {
		          ItemStack item = ItemStack.fromTag(nbt.getCompound(i));
		          if (item.getItem() != Items.AIR) {
		            items++;
		          }
		          giveItem(item);
		        }
		        if (items == 0) {
		          CodeUtilities.chat("§cThere are no items stored in that container!");
		        } else {
		          if (items == 1) CodeUtilities.chat("§aUnpacked §b" + items + "§a item!");
		          else CodeUtilities.chat("§aUnpacked §b" + items + "§a items!");
		        }
		}else {
			CodeUtilities.chat("§cYou need to be in creative mode to use this command!");
		}
	}
	
	public static void giveItem(ItemStack item) {
	    assert MinecraftClient.getInstance().player != null;
	    for (int index = 0; index < MinecraftClient.getInstance().player.inventory.main.size(); index++) {
	      ItemStack i = MinecraftClient.getInstance().player.inventory.main.get(index);
	      ItemStack compareItem = i.copy();
	      compareItem.setCount(item.getCount());
	      if (item == compareItem) {
	        while (i.getCount() < i.getMaxCount() && item.getCount() > 0) {
	          i.setCount(i.getCount() + 1);
	          item.setCount(item.getCount() - 1);
	        }
	      } else {
	        if (i.getItem() == Items.AIR) {
	          assert MinecraftClient.getInstance().interactionManager != null;
	          if (index < 9) MinecraftClient.getInstance().interactionManager.clickCreativeStack(item, index + 36);
	          MinecraftClient.getInstance().player.inventory.main.set(index, item);
	          return;
	        }
	      }
	    }
	  }
	
	public static void register(CommandDispatcher<CottonClientCommandSource> cd) {
		cd.register(ArgumentBuilders.literal("shulker")
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
