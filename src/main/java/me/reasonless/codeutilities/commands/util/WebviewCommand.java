package me.reasonless.codeutilities.commands.util;

import com.mojang.brigadier.context.CommandContext;
import me.reasonless.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.text.Text;

public class WebviewCommand {

  public static int run(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {

    try {
      assert mc.player != null;
      ItemStack i = mc.player.getMainHandStack();
      if (i.getItem() == Items.ENDER_CHEST) {
        CompoundTag nbt = StringNbtReader.parse(
            i.getOrCreateTag().getCompound("PublicBukkitValues")
                .getString("hypercube:codetemplatedata"));
         Text msg = Text.Serializer.fromJson(
            "{\"text\":\"§6[§eBU§6]:§e Click §nHere§e to open the template in the webview.\"}");
        assert msg != null;
        msg.getStyle().setClickEvent(new ClickEvent(Action.OPEN_URL,
            "https://derpystuff.gitlab.io/code/?template=" + nbt.getString("code")));
        mc.player.sendMessage(msg);
        return 1;
      } else {
        CodeUtilities.errorMsg("§cYou need to hold the code template in your main hand!");
      }
    } catch (Exception e) {
      CodeUtilities.errorMsg("§cWhat your holding doesn't seem to be a valid code template!");
    }

    return -1;
  }

}
