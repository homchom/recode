package me.reasonless.codeutilities.commands.util;

import com.mojang.brigadier.context.CommandContext;
import me.reasonless.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import java.util.Date;
import net.minecraft.client.MinecraftClient;

public class PingCommand {

  public static int proxy(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    assert mc.player != null;
    mc.player.sendChatMessage("/server");
    CodeUtilities.pping = new Date().getTime();
    return 1;
  }

  public static int server(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    assert mc.player != null;
    mc.player.sendChatMessage("/plot");
    CodeUtilities.sping = new Date().getTime();
    return 1;
  }

  public static int both(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    assert mc.player != null;
    mc.player.sendChatMessage("/plot");
    CodeUtilities.sping = new Date().getTime();
    mc.player.sendChatMessage("/server");
    CodeUtilities.pping = new Date().getTime();
    return 1;
  }
}
