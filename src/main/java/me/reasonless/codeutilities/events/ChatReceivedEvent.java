package me.reasonless.codeutilities.events;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.reasonless.codeutilities.CodeUtilities;
import me.reasonless.codeutilities.CodeUtilities.PlayMode;
import me.reasonless.codeutilities.commands.util.AfkCommand;
import me.reasonless.codeutilities.commands.util.FriendCommand;
import me.reasonless.codeutilities.objects.AfkMessage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ChatReceivedEvent {

  public static void onMessage(Text message, CallbackInfo info) {

	  if (CodeUtilities.hasblazing) return;
	  
	  if (message.getString().startsWith("§l§l§2 - §a")) {
		  return;
	  }
	  if (message.getString().startsWith("§l§l§4 - §c")) {
		  return;
	  }
	  if (message.getString().startsWith("§l§l§6 - §e")) {
		  return;
	  }
	  if (message.getString().startsWith("§l§l§3 - §b")) {
		  return;
	  }
    
    MinecraftClient mc = MinecraftClient.getInstance();

    boolean cancel = false;

    //Afk Reply
    if (CodeUtilities.afk) {
      if (message.getString().matches("..\\[.r.b.+.r.. -> .r.bYou.r..\\] .r.7.+")) {
        String user = message.getString().split("..\\[.r.b")[1].split(".r.. -> .r.bYou.r..\\] .r.7")[0];
        assert mc.player != null;
        mc.player.sendChatMessage("/msg " + user + " " + AfkCommand.msg);
        AfkCommand.msgs.add(new AfkMessage(message));
      }
    }

    //AutoTip
    if (CodeUtilities.p.getProperty("autotip").equalsIgnoreCase("true")) {
      if (message.getString().contains("You have already tipped this player!")) {
        cancel = true;
      } else if (message.getString().length() > 30) {
        if (message.getString().substring(8).matches(
            "Use §r§6/tip \\w+§r§7 to show your appreciation and get [0-9]+ credits!§r")) {
          String[] msg = message.getString().split(" ");
          assert mc.player != null;
          String player = msg[3].split("§r§7")[0];
          try {
            mc.player.sendChatMessage("/tip " + player);
          } catch (NullPointerException ignored) {}
        }
      }
    }

    //PlayerJoin
    if (CodeUtilities.playerjoin > 0) {
      cancel = true;
      if (message.getString().contains("          ")) {
        CodeUtilities.playerjoin--;
      } else if (CodeUtilities.playerjoin == 1) {
        try {
          String cmd = "/join " + (StringUtils.substringBetween(message.getString(), "[§7ID: ", "§8]"));
          System.out.println(cmd);
          if (cmd.matches("/join [0-9]+")) {
            assert mc.player != null;
            mc.player.sendChatMessage(cmd);
            CodeUtilities.playMode = PlayMode.PLAY;
          } else {
            CodeUtilities.errorMsg("§cAn error occurred while trying to join the plot!");
          }
        } catch (Exception err) {
          CodeUtilities.errorMsg("§c" + err.getMessage());
        }
      }
    }


    //Rejoin
    if (CodeUtilities.rejoin > 0) {
      cancel = true;
      if (message.getString().contains("          ")) {
        CodeUtilities.rejoin--;
      } else if (CodeUtilities.rejoin == 1) {
        if (message.getString().contains("ID:")) {
          String finalId = StringUtils.substringBetween(message.getString(), "[ID: ", "]");
          System.out.println("ID: " + finalId);
          if (finalId.matches("[0-9]+")) {
            new Thread(() -> {
              try {
                Thread.sleep(500);
              } catch (InterruptedException ex) {
                ex.printStackTrace();
              }
              assert mc.player != null;
              mc.player.sendChatMessage("/join " + finalId);
              CodeUtilities.playMode = PlayMode.PLAY;
              CodeUtilities.successMsg("Rejoined plot " + finalId);
            }).start();
          } else {
            CodeUtilities.errorMsg("§cAn error occurred while trying to rejoin the plot!");
          }
        }
      }
    }

    //Proxy and Server Ping
    if (CodeUtilities.sping != 0 || CodeUtilities.pping != 0) {
      if (message.getString().contains("Use /plot help for plot commands.")) {
        CodeUtilities
            .infoMsgYellow("Server Ping: " + (new Date().getTime() - CodeUtilities.sping) + "ms");
        cancel = true;
        CodeUtilities.sping = 0;
      } else if (message.getString().contains("You are currently connected to")) {
        CodeUtilities
            .infoMsgYellow("Proxy Ping: " + (new Date().getTime() - CodeUtilities.pping) + "ms");
        cancel = true;
      } else if (message.getString()
          .contains("You may connect to the following servers at this time:")) {
        cancel = true;
        CodeUtilities.pping = 0;
      }
    }

    //Location Highlighter / Relative plot coords
    if (message.getString().matches("You are now in dev mode\\.")) {
      CodeUtilities.playMode = PlayMode.DEV;
      assert mc.player != null;
      BlockPos pos = mc.player.getBlockPos();
      pos = pos.down(pos.getY());
      pos = pos.east(10);
      pos = pos.north(10);
      CodeUtilities.plotPos = pos;
    }

    //FriendList
    if (FriendCommand.listFriends > 0) {
      if (message.getString().matches("Error: Could not find that player\\.")
          || message.getString()
          .matches("An internal error occurred while attempting to perform this command\\.")) {
        FriendCommand.listFriends -= 2;
        cancel = true;
        CodeUtilities.infoMsgYellow("§e" + FriendCommand.friends.get(0) + "§7 - §cOffline");
        FriendCommand.friends.remove(0);
      }
      if (message.getString().contains("                                     ")) {
        FriendCommand.listFriends--;
        cancel = true;
      } else if (Math.round((FriendCommand.listFriends - 1) / 2) * 2
          == FriendCommand.listFriends - 1) {
        try {
          String msg = "§6Unknown Response";
          if (message.getString().contains("is currently at")) {
            msg =
                "At " + message.getString().split("Server: ")[1].substring(2)
                    + " Spawn";
          }
          if (message.getString()
              .contains("is currently §6coding")) {
            msg = "Coding on " + StringUtils.substringBetween(message.getString(), "§6§l≫§r ", "§8 [§7ID: ");
          }
          if (message.getString()
              .contains("is currently §6building")) {
        	  msg = "Building on " + StringUtils.substringBetween(message.getString(), "§6§l≫§r ", "§8 [§7ID: ");
          }
          if (message.getString()
              .contains("is currently §6playing")) {
        	  msg = "Playing on " + StringUtils.substringBetween(message.getString(), "§6§l≫§r ", "§8 [§7ID: ");
          }

          cancel = true;
          CodeUtilities.infoMsgYellow("§e" + FriendCommand.friends.get(0) + "§7 - §a" + msg);
          FriendCommand.friends.remove(0);
        } catch (Exception err) {
          CodeUtilities.errorMsg("§cError: Something failed :(");
          FriendCommand.friends.clear();
          FriendCommand.listFriends = 0;
          err.printStackTrace();
        }
      }
    }

    if (cancel) {
      System.out.println("[Cancelled] " + message.getString());
        info.cancel();
    }

  }

}
