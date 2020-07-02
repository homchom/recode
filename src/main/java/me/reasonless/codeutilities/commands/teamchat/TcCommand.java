package me.reasonless.codeutilities.commands.teamchat;

import com.mojang.brigadier.context.CommandContext;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class TcCommand {

  public static int send(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    String msg = ctx.getArgument("msg", String.class);
    if (Teamchat.connected()) {
      Teamchat.socket.emit("sendMsg", msg);
      return 1;
    }
    return -1;
  }

  public static int create(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    if (Teamchat.connected()) {
      Teamchat.print("Creating TeamChat group...");
      Teamchat.socket.emit("createRoom");
      return 1;
    }
    return -1;
  }

  public static int invite(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    String player = ctx.getInput().split(" ")[2];
    if (Teamchat.connected()) {
      Teamchat.print("§aInviting " + player + " to join your TeamChat group...");
      Teamchat.socket.emit("inviteToRoom", player);
      return 1;
    }
    return -1;
  }

  public static int kick(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    String player = ctx.getInput().split(" ")[2];
    if (Teamchat.connected()) {
      Teamchat.print("§aKicking " + player + " from join your TeamChat group...");
      Teamchat.socket.emit("kick", player);
      return 1;
    }
    return -1;
  }

  public static int reconnect(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    if (Teamchat.connected()) {
      Teamchat.print("§aAlready connected!");
      return 1;
    }
    return -1;
  }

  public static int accept(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    String id = ctx.getInput().split(" ")[2];
    if (Teamchat.connected()) {
      Teamchat.print("§aAccepting invite...");
      Teamchat.socket.emit("acceptInvite", id);
      return 1;
    }
    return -1;
  }

  public static int leave(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    if (Teamchat.connected()) {
      Teamchat.print("Leaving TeamChat group...");
      Teamchat.socket.emit("leaveRoom");
      return 1;
    }
    return -1;
  }

  public static int auto(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    if (Teamchat.connected()) {
      Teamchat.autoMsg = !Teamchat.autoMsg;
      if (Teamchat.autoMsg) {
        Teamchat.print("§aAll msgs are now send to the TeamChat group!");
      } else {
        Teamchat.print("§aAll msgs are now send to the public chat!");
      }
      return 1;

    }
    return -1;
  }
}
