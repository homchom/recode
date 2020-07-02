package me.reasonless.codeutilities.commands.teamchat;

import io.socket.client.IO;
import io.socket.client.Socket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Text.Serializer;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

public class Teamchat {

  public static Socket socket = null;
  public static String room = "";
  public static boolean autoMsg = false;

  private static void loadListeners(Socket socket) {
    try {
      socket.on("connect", args -> print("§aConnected to the Teamchat server."));

      socket.on("joinedRoom", args -> print("§aJoined TeamChat group!"));

      socket.on("leftRoom", args -> {
        print("§aLeft TeamChat group!");
        autoMsg = false;
        room = "";
      });

      socket.on("disconnect", args -> print("§cDisconnected from the Teamchat server."));

      socket.on("invalidSession", args -> print("§cInvalid session id."));

      socket.on("alreadyInRoom", args -> print(
          "§cYou are already in a TeamChat group! Do §4/teamchat leave§c to leave it!"));

      socket.on("createdRoom", args -> {
        try {
          room = ((JSONObject) args[0]).getString("id");
        } catch (Exception e) {
          e.printStackTrace();
          print(
              "§cError while handling packet! If this keeps happening please send me (BlazeMCworld) the error log!");
        }
        print("§aCreated TeamChat group!\n§7(ID: " + room + ")");
      });

      socket.on("userAlreadyInRoom",
          args -> {
            try {
              print("§c" + ((JSONObject) args[0]).getString("player")
                  + " is already in a TeamChat group!");
            } catch (Exception e) {
              e.printStackTrace();
              print(
                  "§cError while handling packet! If this keeps happening please send me (BlazeMCworld) the error log!");
            }
          });

      socket.on("userLeave", args -> {
        try {
          print(
              "§c" + ((JSONObject) args[0]).getString("user") + " left the TeamChat group!");
        } catch (Exception e) {
          e.printStackTrace();
          print(
              "§cError while handling packet! If this keeps happening please send me (BlazeMCworld) the error log!");
        }
      });

      socket
          .on("ownerLeave", args -> {
            try {
              print(
                  "The TeamChat group owner left! New owner: " + ((JSONObject) args[0])
                      .getString("newOwner"));
            } catch (Exception e) {
              e.printStackTrace();
              print(
                  "§cError while handling packet! If this keeps happening please send me (BlazeMCworld) the error log!");
            }
          });

      socket.on("invite", args -> {
        try {
          Text msg = Serializer.fromJson("{\"text\":\"" + StringEscapeUtils.escapeJson(
              "§5[§dTC§5]:§d You have been invited by " + ((JSONObject) args[0])
                  .getString("invitor")
                  + " to join a TeamChat group! §lClick here§d to join!") + "\"}");
          Style clickevent = new Style();
          clickevent.setClickEvent(new ClickEvent(Action.RUN_COMMAND,
              "/teamchat accept " + ((JSONObject) args[0]).getString("id")));
          assert msg != null;
          msg.setStyle(clickevent);
          assert MinecraftClient.getInstance().player != null;
          MinecraftClient.getInstance().player.sendMessage(msg);
        } catch (Exception e) {
          e.printStackTrace();
          print(
              "§cError while handling packet! If this keeps happening please send me (BlazeMCworld) the error log!");
        }
      });

      socket.on("invited", args -> {
        try {
          print(
              "§aInvited " + ((JSONObject) args[0]).getString("player")
                  + " to your TeamChat group!");
        } catch (Exception e) {
          e.printStackTrace();
          print(
              "§cError while handling packet! If this keeps happening please send me (BlazeMCworld) the error log!");
        }
      });

      socket.on("notOwner", args -> {
        try {
          print(
              "§cYou are not the TeamChat group owner! Owner: " + ((JSONObject) args[0])
                  .getString("owner"));
        } catch (Exception e) {
          e.printStackTrace();
          print(
              "§cError while handling packet! If this keeps happening please send me (BlazeMCworld) the error log!");
        }
      });

      socket.on("notInRoom", args -> print(
          "§cYou aren't in a TeamChat group! Do §4/teamchat create§c to create one!"));

      socket.on("playerNotOnline", args -> print(
          "§cThat player isn't online, doesn't have the mod installed or isn't connected to the TeamChat server!"));

      socket.on("userJoinRoom",
          args -> {
            try {
              print("§a" +
                  ((JSONObject) args[0]).getString("player") + " joined the TeamChat group!");
            } catch (Exception e) {
              e.printStackTrace();
              print(
                  "§cError while handling packet! If this keeps happening please send me (BlazeMCworld) the error log!");
            }
          });

      socket.on("roomNotFound",
          args -> print("§cUnable to join TeamChat group! Ask for a new invite!"));

      socket.on("receiveMsg", args -> {
        try {
          print(
              ((JSONObject) args[0]).getString("user") + ": §r" + ((JSONObject) args[0])
                  .getString("msg"));
        } catch (Exception e) {
          e.printStackTrace();
          print(
              "§cError while handling packet! If this keeps happening please send me (BlazeMCworld) the error log!");
        }
      });

      socket.on("kicked", args -> {
        print("§cYou have been kicked from the TeamChat group!");
        room = "";
        autoMsg = false;
      });

      socket.on("userKick", args -> {
        try {
          print("§c" + ((JSONObject) args[0]).getString("user")
              + " has been kicked from the TeamChat group!");
        } catch (Exception e) {
          e.printStackTrace();
          print(
              "§cError while handling packet! If this keeps happening please send me (BlazeMCworld) the error log!");
        }
      });

      socket.on("userNotInRoom", args -> print("§cThat user is not in your TeamChat group!"));

      socket.on("cantKickSelf",
          args -> print("§cYou can't kick yourself. Do §4/teamchat leave§c instead!"));

      socket.on("cantInviteSelf", args -> print("§cYou can't invite yourself lol."));
    } catch (Exception e) {
      e.printStackTrace();
      print(
          "§cError while handling packet! If this keeps happening please send me (BlazeMCworld) the error log!");
    }

  }

  public static boolean connected() {
    if (socket == null || !socket.connected()) {
      try {
        print("Teamchat is currently wip!");

       IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = true;
        opts.query = "sessionid=" + MinecraftClient.getInstance().getSession().getSessionId() + "&username="
            + MinecraftClient.getInstance().getSession().getUsername();
        opts.secure = true;
        socket = IO.socket("https://df-teamchat.glitch.me/", opts);
        loadListeners(socket);
        print("§cNot connected! Reconnecting...");
        socket.connect();

      } catch (Exception e) {
        e.printStackTrace();
        print(
            "§cTeamchat error! If this keeps happing, please send me (BlazeMCworld) the error log.");
      }
      return false;
    }
    return true;
  }

  static void print(String msg) {
    //afaik this is not needed when the mod is build, but while running the mod in the dev env colorcodes bug
    msg = "§5[§dTC§5]:§d " + msg;
    assert MinecraftClient.getInstance().player != null;
    MinecraftClient.getInstance().player.sendMessage(new LiteralText(msg));
    //ik that its deprecated but im too lazy to search for an non deprecated one
  }

}
