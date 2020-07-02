package me.reasonless.codeutilities.commands.item;

import com.mojang.brigadier.context.CommandContext;
import me.reasonless.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.Base64;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.Tag;

public class CustomheadCommand {

  public static int run(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
    try {
      assert mc.player != null;
      if (mc.player.isCreative()) {
        ItemStack item = new ItemStack(Items.PLAYER_HEAD);
        String id = UUID.randomUUID().toString();
        String value = ctx.getArgument("value", String.class);
        
        String finalValue = value;
        
        if (value.contains(".minecraft.net")) {
        	Charset charset = StandardCharsets.UTF_8;
        	String rawString = "{\"textures\":{\"SKIN\":{\"url\":\"" + value +"\"}}}";
        	
        	byte[]a = Base64.getEncoder().encode(rawString.getBytes(charset));
        	finalValue = new String(a, charset);
        }
        
        CompoundTag nbt = StringNbtReader.parse("{SkullOwner:{Id:\"" + id + "\",Properties:{textures:[{Value:\"" + finalValue + "\"}]}}}");
        item.setTag(nbt);
        CodeUtilities.giveCreativeItem(item);
        return 1;
      } else {
    	  CodeUtilities.errorMsg("§cYou need to be in creative for this command to work!");
        return -1;
      }
    } catch (Exception e) {
    	CodeUtilities.errorMsg("§c" + e.getMessage());
    }
    return -1;
  }

}
