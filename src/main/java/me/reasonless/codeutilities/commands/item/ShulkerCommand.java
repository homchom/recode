package me.reasonless.codeutilities.commands.item;

import com.mojang.brigadier.context.CommandContext;

import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.reasonless.codeutilities.CodeUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;

public class ShulkerCommand {

  public static int run(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    assert mc.player != null;
    if (mc.player.isCreative()) {
      try {
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
          CodeUtilities.errorMsg("§cThere are no items stored in that container!");
        } else {
          CodeUtilities.successMsg("Unpacked " + items + " items!");
        }
        return 1;
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      CodeUtilities.errorMsg("§cYou need to be in creative for this command to work!");
    }

    return -1;
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

}
