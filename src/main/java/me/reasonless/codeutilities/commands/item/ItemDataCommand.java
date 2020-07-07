package me.reasonless.codeutilities.commands.item;

import com.mojang.brigadier.context.CommandContext;

import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ItemDataCommand {

  public static int run(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    assert mc.player != null;
    Text msg1 = new LiteralText("§l§l§6 - §r").append(mc.player.getMainHandStack().getTag().toText());
    mc.player.sendMessage(msg1, false);
    LiteralText msg2 = new LiteralText("§3 - §bClick §lhere§b to copy!");
    msg2.styled((style)-> {return style.withClickEvent(new ClickEvent(Action.COPY_TO_CLIPBOARD, mc.player.getMainHandStack().getOrCreateTag().toString())); });
    mc.player.sendMessage(msg2, false);
    return 1;
  }

}
