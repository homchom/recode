package me.reasonless.codeutilities.commands.item;

import com.mojang.brigadier.context.CommandContext;
import me.reasonless.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;

public class HideflagsCommand {

  public static int run(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    try {
      assert mc.player != null;
      if (mc.player.isCreative()) {
        ItemStack item = mc.player.getMainHandStack();
        CompoundTag nbt = item.getOrCreateTag();
        int hideflags = 63;
        try {
          if (!ctx.getArgument("Enchantments", Boolean.class)) {
            hideflags -= 1;
          }
          if (!ctx.getArgument("Modifiers", Boolean.class)) {
            hideflags -= 2;
          }
          if (!ctx.getArgument("Unbreakable", Boolean.class)) {
            hideflags -= 4;
          }
          if (!ctx.getArgument("CanDestroy", Boolean.class)) {
            hideflags -= 8;
          }
          if (!ctx.getArgument("CanPlaceOn", Boolean.class)) {
            hideflags -= 16;
          }
          if (!ctx.getArgument("HideOthers", Boolean.class)) {
            hideflags -= 32;
          }
        } catch (Exception ignored) {
        }
        nbt.put("HideFlags", IntTag.of(hideflags));
        item.setTag(nbt);
        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
        if (hideflags == 63) {
          CodeUtilities.successMsg("All flags are now hidden!");
        } else if (hideflags == 0) {
          CodeUtilities.successMsg("All flags are now shown!");
        } else {
        	CodeUtilities.successMsg("Specified flags are now hidden!");
          CodeUtilities.infoMsgYellow("Tip: The argument order is: Enchantments, Modifiers, Unbreakable, CanDestroy, CanPlaceOn, HideOthers");
        }
        return 1;
      } else {
        CodeUtilities.errorMsg("You need to be in creative for this command to work!");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }

}
