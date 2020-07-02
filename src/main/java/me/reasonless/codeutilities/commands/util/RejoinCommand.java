package me.reasonless.codeutilities.commands.util;

import com.mojang.brigadier.context.CommandContext;
import me.reasonless.codeutilities.CodeUtilities;
import me.reasonless.codeutilities.CodeUtilities.PlayMode;
import me.reasonless.codeutilities.events.ActionbarReceivedEvent;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class RejoinCommand {

  public static int run(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    assert mc.player != null;
    ActionbarReceivedEvent.autofly = 0;
    mc.player.sendChatMessage("/locate");
    mc.player.sendChatMessage("/spawn");
    CodeUtilities.playMode = PlayMode.SPAWN;
    CodeUtilities.rejoin = 2;
    return 1;
  }
}
