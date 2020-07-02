package me.reasonless.codeutilities.events;

import me.reasonless.codeutilities.CodeUtilities;
import me.reasonless.codeutilities.CodeUtilities.PlayMode;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class TickEvent {

  public static void register() {
    MinecraftClient mc = MinecraftClient.getInstance();
    ClientTickCallback.EVENT.register(minecraftClient -> {
      //AutoRc
      CodeUtilities.lastrc++;
      if (mc.player != null && CodeUtilities.lastrc > 100 && !mc.isInSingleplayer() && CodeUtilities.p
          .getProperty("autorc").equalsIgnoreCase("true") && mc.player.isCreative()) {
        CodeUtilities.playMode = PlayMode.DEV;
        int index = 0;
        String[] items = {"diamond_block", "oak_planks", "cobblestone", "lapis_ore",
            "emerald_ore", "air", "air", "air", "iron_ingot", "coal_block", "purpur_block",
            "prismarine", "end_stone", "air", "air", "air", "air", "written_book", "iron_block",
            "obsidian", "netherrack", "red_nether_bricks", "air", "air", "air", "air", "air",
            "gold_block", "bricks", "mossy_cobblestone", "lapis_block", "emerald_block", "air",
            "air", "air", "arrow"};

        for (ItemStack i : mc.player.inventory.main) {
          if (!i.getItem().toString().equalsIgnoreCase(items[index])) {
            break;
          }
          index++;
          if (index == items.length) {
            CodeUtilities.lastrc = 0;
            mc.player.sendChatMessage("/rc");
          }
        }
      }
    });
  }
}
