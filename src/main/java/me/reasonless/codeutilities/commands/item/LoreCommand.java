package me.reasonless.codeutilities.commands.item;

import com.mojang.brigadier.context.CommandContext;
import me.reasonless.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Text.Serializer;
import org.apache.commons.lang3.StringEscapeUtils;

public class LoreCommand {

  public static int add(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    try {
      assert mc.player != null;
      if (mc.player.isCreative()) {
        ItemStack item = mc.player.getMainHandStack();
        CompoundTag nbt = item.getOrCreateTag();
        CompoundTag display = nbt.getCompound("display");
        ListTag lore = display.getList("Lore", 8);
        lore.add(StringTag.of(StringTag.escape(ctx.getArgument("lore", String.class)).replaceAll("&", "§")));
        display.put("Lore", lore);
        nbt.put("display", display);
        item.setTag(nbt);
        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
        CodeUtilities.successMsg(
            "Added §5§o" + ctx.getArgument("lore", String.class).replaceAll("&", "§") + "§e!");
        showLore(lore);
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
        CompoundTag display = nbt.getCompound("display");
        ListTag lore = display.getList("Lore", 8);
        lore.clear();
        display.put("Lore", lore);
        nbt.put("display", display);
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

  public static int insert(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    try {
      assert mc.player != null;
      if (mc.player.isCreative()) {
        ItemStack item = mc.player.getMainHandStack();
        CompoundTag nbt = item.getOrCreateTag();
        CompoundTag display = nbt.getCompound("display");
        ListTag lore = display.getList("Lore", 8);
        lore.add(ctx.getArgument("line", Integer.class) - 1,
            StringTag.of(StringTag.escape(ctx.getArgument("lore", String.class))));
        display.put("Lore", lore);
        nbt.put("display", display);
        item.setTag(nbt);
        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
        CodeUtilities.successMsg(
            "Inserted §5§o" + ctx.getArgument("lore", String.class).replaceAll("&", "§") + " §eat "
                + ctx.getArgument("line", Integer.class) + "!");
        showLore(lore);
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
        CompoundTag display = nbt.getCompound("display");
        ListTag lore = display.getList("Lore", 8);
        lore.remove(ctx.getArgument("line", Integer.class) - 1);
        display.put("Lore", lore);
        nbt.put("display", display);
        item.setTag(nbt);
        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
        CodeUtilities.successMsg("Removed line " + ctx.getArgument("line", Integer.class) + "!");
        showLore(lore);
        return 1;
      } else {
        CodeUtilities.errorMsg("§cYou need to be in creative for this command to work!");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }

  public static int set(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    try {
      assert mc.player != null;
      if (mc.player.isCreative()) {
        ItemStack item = mc.player.getMainHandStack();
        CompoundTag nbt = item.getOrCreateTag();
        CompoundTag display = nbt.getCompound("display");
        ListTag lore = display.getList("Lore", 8);
        lore.set(ctx.getArgument("line", Integer.class) - 1,
            StringTag.of(StringTag.escape(ctx.getArgument("lore", String.class)).replaceAll("&", "§")));
        display.put("Lore", lore);
        nbt.put("display", display);
        item.setTag(nbt);
        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
        CodeUtilities.successMsg(
            "Changed line " + ctx.getArgument("line", Integer.class) + " to §5§o" + ctx
                .getArgument("lore", String.class).replaceAll("&", "§") + "§e!");
        showLore(lore);
        return 1;
      } else {
        CodeUtilities.errorMsg("§cYou need to be in creative for this command to work!");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }

  private static void showLore(ListTag lore) {
    int index = 0;
    CodeUtilities.infoMsgYellow("New lore text:");
    for (Tag line : lore) {
      String text = Objects.requireNonNull(Serializer.fromJson(line.asString())).asString();
      index++;
      text = "§b" + index + ". §5§o" + text;
      assert MinecraftClient.getInstance().player != null;
      MinecraftClient.getInstance().player.sendMessage(new LiteralText(text), false);
    }
  }

}
