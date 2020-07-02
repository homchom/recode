package me.reasonless.codeutilities.commands.util;

import com.mojang.brigadier.context.CommandContext;
import me.reasonless.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class PJoinCommand {

  public static int run(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    try {
      CodeUtilities.playerjoin = 2;
      assert mc.player != null;
      mc.player.sendChatMessage("/locate " + ctx.getInput().split(" ")[1]);
    } catch (Exception e) {
      CodeUtilities.errorMsg(e.getMessage());
    }
    return 1;
  }

}
