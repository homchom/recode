package me.reasonless.codeutilities.commands.util;

import com.mojang.brigadier.context.CommandContext;
import me.reasonless.codeutilities.CodeUtilities;
import me.reasonless.codeutilities.objects.AfkMessage;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class AfkCommand {

  public static String msg = CodeUtilities.p.getProperty("afkmsg");
  public static List<AfkMessage> msgs = new ArrayList<>();

  public static int run(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    try {
      msg = ctx.getArgument("message", String.class);
      CodeUtilities.p.setProperty("afkmsg", msg);
      CodeUtilities.updateConfig();
      CodeUtilities.successMsg("Changed Auto-Reply message to §o" + msg);
      CodeUtilities.infoMsgYellow("Type /afk without arguments to enter afk mode.");
    } catch (Exception e) {
      if (!CodeUtilities.afk) {
    	  CodeUtilities.successMsg("Enabled AFK mode.");
    	  CodeUtilities.afk = true;
      } else {
    	  CodeUtilities.errorMsg("§cAFK mode is already enabled!");
    	  CodeUtilities.errorMsg("§cMove around to disable it!");
      }
    }
    return -1;
  }

}
