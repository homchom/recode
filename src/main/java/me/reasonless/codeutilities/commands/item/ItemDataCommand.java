package me.reasonless.codeutilities.commands.item;

import com.mojang.brigadier.context.CommandContext;
import me.reasonless.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class ItemDataCommand {

  public static int run(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    assert mc.player != null;
    CodeUtilities.infoMsgYellow("§r" + mc.player.getMainHandStack().getOrCreateTag().toText().asFormattedString());
    Text msg = new LiteralText("§3 - §bClick §lhere§b to copy!");
    Style style = new Style();
    style.setClickEvent(new ClickEvent(Action.COPY_TO_CLIPBOARD, mc.player.getMainHandStack().getOrCreateTag().toString()));
    msg.setStyle(style);
    mc.player.sendMessage(msg);
    return 1;
  }

}
