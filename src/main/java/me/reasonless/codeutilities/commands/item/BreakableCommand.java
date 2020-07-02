package me.reasonless.codeutilities.commands.item;

import com.mojang.brigadier.context.CommandContext;

import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.reasonless.codeutilities.CodeUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class BreakableCommand {

  public static int run(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    try {
      assert mc.player != null;
      if (mc.player.isCreative()) {
        ItemStack item = mc.player.getMainHandStack();
        CompoundTag nbt = item.getOrCreateTag();
        nbt.putBoolean("Unbreakable", false); //1b
        item.setTag(nbt);
        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
        CodeUtilities.successMsg("The item you're holding is now breakable!");
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
