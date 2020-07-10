package io.github.codeutilities.commands.item;

import io.github.codeutilities.CodeUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class GiveCommand {

  static MinecraftClient mc = MinecraftClient.getInstance();

  public static void run(ItemStack item, int count) {
    item.setCount(count);
    assert mc.player != null;
    if (mc.player.isCreative()) {
      if (count >= 1) {
        if (count <= item.getMaxCount()) {
          CodeUtilities.giveCreativeItem(item);
        } else {
          CodeUtilities.chat("§cMaximum item count for " + item.getName() + "is " + item.getMaxCount() + "!");
        }
      } else {
        CodeUtilities.chat("§cMinimum item count is 1!");
      }
    } else {
      CodeUtilities.chat("§cYou need to be in creative for this command to work.");
    }
  }

  public static void clipboard() {
    String clipboard;
    try {
      clipboard = mc.keyboard.getClipboard();
    } catch (Exception e) {
      CodeUtilities.chat("§cUnable to get Clipboard");
      return;
    }
    if (clipboard.startsWith("/")) {
      clipboard = clipboard.substring(1);
    }

    if (clipboard.startsWith("give ")) {
      clipboard = clipboard.substring(5);
    }

    if (clipboard.startsWith("@p ") || clipboard.startsWith("@s ")) {
      clipboard = clipboard.substring(3);
    }

    assert mc.player != null;
    mc.player.sendChatMessage("/give " + clipboard);

  }

}
