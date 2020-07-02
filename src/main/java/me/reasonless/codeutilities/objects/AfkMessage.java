package me.reasonless.codeutilities.objects;

import java.util.Date;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class AfkMessage {

  Text msg;
  Date date;

  public AfkMessage(Text msg) {
    this.msg = msg;
    this.date = new Date();
  }

  public void print(MinecraftClient mc) {
    long mins = (new Date().getTime() - date.getTime())/60000;
    assert mc.player != null;
    if (mins == 0) {
      mc.player.sendMessage(new LiteralText("§aJust now: ").append(msg));
    } else if (mins == 1) {
      mc.player.sendMessage(new LiteralText("§a1min ago: ").append(msg));
    } else {
      mc.player.sendMessage(new LiteralText("§a" + mins + "mins ago: ").append(msg));
    }

  }

}
