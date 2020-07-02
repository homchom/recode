package me.reasonless.codeutilities.commands.item;

import com.mojang.brigadier.context.CommandContext;
import me.reasonless.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.arguments.ItemStackArgument;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

public class CanPlaceOnCommand {

  public static int add(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    try {
      assert mc.player != null;
      if (mc.player.isCreative()) {
        ItemStack item = mc.player.getMainHandStack();
        CompoundTag nbt = item.getOrCreateTag();
        ListTag canplaceon = nbt.getList("CanPlaceOn", 8);
        canplaceon
            .add(StringTag.of(ctx.getArgument("block", ItemStackArgument.class).getItem().toString()));
        nbt.put("CanPlaceOn", canplaceon);
        item.setTag(nbt);
        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
        CodeUtilities
            .successMsg("Added " + ctx.getArgument("block", ItemStackArgument.class).getItem().toString() + "!");
        return 1;
      } else {
        CodeUtilities.errorMsg("§cYou need to be in creative for this command to work!");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }

  public static int remove(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    try {
      assert mc.player != null;
      if (mc.player.isCreative()) {
        ItemStack item = mc.player.getMainHandStack();
        CompoundTag nbt = item.getOrCreateTag();
        ListTag canplaceon = nbt.getList("CanPlaceOn", 8);
        for (int i = 0; i < canplaceon.size(); i++) {
          if (canplaceon.get(i).toString().equalsIgnoreCase(
              "\"minecraft:" + ctx.getArgument("block", ItemStackArgument.class).getItem().toString()
                  + "\"")) {
            canplaceon.remove(i);
          }
        }
        item.setTag(nbt);
        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
        CodeUtilities.successMsg(
            "Removed " + ctx.getArgument("block", ItemStackArgument.class).getItem().toString() + "!");
        return 1;
      } else {
        CodeUtilities.errorMsg("§cYou need to be in creative for this command to work!");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }

  public static int clear(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    try {
      assert mc.player != null;
      if (mc.player.isCreative()) {
        ItemStack item = mc.player.getMainHandStack();
        CompoundTag nbt = item.getOrCreateTag();
        ListTag canplaceon = nbt.getList("CanPlaceOn", 8);
        canplaceon.clear();
        nbt.put("CanPlaceOn", canplaceon);
        item.setTag(nbt);
        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
        CodeUtilities.successMsg("Cleared!");
        return 1;
      } else {
        CodeUtilities.errorMsg("§cYou need to be in creative for this command to work!");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }
}
