package me.reasonless.codeutilities.commands.util;

import com.mojang.brigadier.context.CommandContext;
import me.reasonless.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class UuidCommand {

  public static int run(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    String url = "https://api.mojang.com/users/profiles/minecraft/" + ctx.getInput().split(" ")[1];
    try {
      String UUIDJson = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
      if(UUIDJson.isEmpty()) {
        CodeUtilities.errorMsg("§cUnknown player!");
        return -1;
      }
      JSONObject json = new JSONObject(UUIDJson);
      String uuid = json.getString("id");
      mc.player.sendChatMessage("/txt " + fromTrimmed(uuid));
    } catch (IOException | JSONException e) {
      CodeUtilities.errorMsg("§cUnknown Player!");
      e.printStackTrace();
    }
    return -1;
  }


  //Credit: https://www.spigotmc.org/threads/free-code-easily-convert-between-trimmed-and-full-uuids.165615/
  public static String fromTrimmed(String trimmedUUID) throws IllegalArgumentException{
    if(trimmedUUID == null) throw new IllegalArgumentException();
    StringBuilder builder = new StringBuilder(trimmedUUID.trim());
    try {
      builder.insert(20, "-");
      builder.insert(16, "-");
      builder.insert(12, "-");
      builder.insert(8, "-");
    } catch (StringIndexOutOfBoundsException e){
      throw new IllegalArgumentException();
    }
    return builder.toString();
  }
}
