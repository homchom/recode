package me.reasonless.codeutilities.commands.item;

import com.mojang.brigadier.context.CommandContext;
import me.reasonless.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.arguments.ItemStackArgument;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;

public class GiveCommand {

  public static int run(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    try {
      Item id = Items.STONE;
      int amount = 1;
      String nbt = "{}";
      try {
        id = ctx.getArgument("id", ItemStackArgument.class).getItem();
        amount = ctx.getArgument("amount", Integer.class);
        nbt = ctx.getArgument("nbt", String.class);
      } catch (IllegalArgumentException ignore) {
      }
      if (amount > new ItemStack(id).getMaxCount()) amount = new ItemStack(id).getMaxCount();

      nbt = nbt.replace("&", "§");

      if (nbt.equalsIgnoreCase("cb")) {
        System.setProperty("java.awt.headless", "false");
        nbt = mc.keyboard.getClipboard();
      } else {
        if (nbt.length() > 50) {
          CodeUtilities.infoMsgYellow("§6Tip:§e Do /give <item> <amount> §lcb§e to use the clipboard as the item nbt");
        }
      }

      if (!nbt.startsWith("{")) {
        nbt = "{" + nbt + "}";
      }

      assert mc.player != null;
      if (mc.player.isCreative()) {
        ItemStack item = new ItemStack(id);
        item.setCount(amount);
        if (nbt.equalsIgnoreCase("cb")) {
          System.setProperty("java.awt.headless", "false");
          nbt = mc.keyboard.getClipboard();
        }
        item.setTag(StringNbtReader.parse(nbt));
        CodeUtilities.giveCreativeItem(item);
        return 1;
      } else {
        CodeUtilities.errorMsg("§cYou need to be in creative for this command to work!");
        return -1;
      }
    } catch (Exception e) {
      CodeUtilities.errorMsg("§cError: " + e.getMessage());
    }
    return -1;
  }
}
