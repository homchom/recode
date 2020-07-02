package me.reasonless.codeutilities.commands.util;

import com.mojang.brigadier.context.CommandContext;
import me.reasonless.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.MinecraftClient;

public class FriendCommand {

  public static int listFriends = 0;
  public static List<String> friends = new ArrayList<>();

  public static int add(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    String player = ctx.getInput().split(" ")[2];
    assert mc.player != null;
    if (!player.equalsIgnoreCase(mc.player.getName().asString())) {
      List<String> friends = new ArrayList<>();
      if (CodeUtilities.p.getProperty("friends") != null) {
        friends = new LinkedList<>(
            Arrays.asList(CodeUtilities.p.getProperty("friends").split(",")));
      }
      if (friends.contains(player)) {
        CodeUtilities.errorMsg("§cThat player is already in the friend list!");
        return -1;
      }
      friends.add(player);
      Collections.sort(friends);
      CodeUtilities.p.setProperty("friends", String.join(",", friends));
      CodeUtilities.updateConfig();
      CodeUtilities.successMsg("§aAdded " + player + " to your friends!");
    } else {
      CodeUtilities.errorMsg("§cYou can't add yourself as friend!");
    }
    return 1;
  }

  public static int remove(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    String player = ctx.getInput().split(" ")[2];
    List<String> friends = new ArrayList<>();
    if (CodeUtilities.p.getProperty("friends") != null) {
      friends = new LinkedList<>(
          Arrays.asList(CodeUtilities.p.getProperty("friends").split(",")));
    }
    if (friends.contains(player)) {
      CodeUtilities.successMsg("§aRemoved " + player + " from your friend list!");
      friends.remove(player);
      CodeUtilities.p.setProperty("friends", String.join(",", friends));
      CodeUtilities.updateConfig();
      return 1;
    }
    CodeUtilities.errorMsg("§cThat player isn't in your friend list!");
    return -1;
  }

  public static int list(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    List<String> friends = new ArrayList<>();
    if (CodeUtilities.p.getProperty("friends") != null) {
      friends = new LinkedList<>(
          Arrays.asList(CodeUtilities.p.getProperty("friends").split(",")));
    }
    if (friends.contains("")) {
      friends.remove("");
      CodeUtilities.p.setProperty("friends", String.join(",", friends));
      CodeUtilities.updateConfig();
    }

    if (friends.size() == 0) {
      CodeUtilities.errorMsg("§cYou don't have any friends ):");
      return 1;
    }

    CodeUtilities.infoMsgYellow("§eFriends (" + friends.size() + "):");
    FriendCommand.friends = friends;
    List<String> finalFriends = friends;
    Collections.sort(finalFriends);
    Collections.sort(FriendCommand.friends);
    new Thread(() -> {
      int count = finalFriends.size();
      for (int i = 0; i < count; i++) {
        System.out.println(finalFriends.toString());
        assert mc.player != null;
        mc.player.sendChatMessage("/locate " + finalFriends.get(0));
        System.out.println("/locate " + finalFriends.get(0));
        listFriends = 2;
        do {
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        } while (listFriends > 0);
      }
    }).start();
    return 1;
  }
}
