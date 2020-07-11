package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.gui.CustomHeadSearchGui;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;

public class CustomHeadCommand {

   static MinecraftClient mc = MinecraftClient.getInstance();

   public static void run(String value) throws CommandSyntaxException {
      assert mc.player != null;
      if (mc.player.isCreative()) {
         ItemStack item = new ItemStack(Items.PLAYER_HEAD);

         if (value.contains(".minecraft.net")) {
            Charset charset = StandardCharsets.UTF_8;
            String rawString = "{\"textures\":{\"SKIN\":{\"url\":\"" + value + "\"}}}";

            byte[] a = Base64.getEncoder().encode(rawString.getBytes(charset));
            value = new String(a, charset);
         }

         CompoundTag nbt = StringNbtReader
             .parse("{SkullOwner:{Id:" + genId() + ",Properties:{textures:[{Value:\"" + value
                 + "\"}]}}}");
         item.setTag(nbt);
         CodeUtilities.giveCreativeItem(item);
      } else {
         CodeUtilities.chat("§cYou need to be in creative for this command to work!");
      }
   }

   public static void register(CommandDispatcher<CottonClientCommandSource> cd) {
      cd.register(ArgumentBuilders.literal("customhead")
          .then(ArgumentBuilders.literal("search")
              .executes(ctx -> {
                 try {
                    CodeUtilities.openGuiAsync(new CustomHeadSearchGui());
                    return 1;
                 } catch (Exception err) {
                    CodeUtilities.chat("§cError while executing command.");
                    err.printStackTrace();
                    return -1;
                 }
              })
          )
          .then(ArgumentBuilders.argument("value", StringArgumentType.greedyString())
              .executes(ctx -> {
                 try {
                    run(ctx.getArgument("value", String.class));
                    return 1;
                 } catch (Exception err) {
                    CodeUtilities.chat("§cError while executing command.");
                    err.printStackTrace();
                    return -1;
                 }
              })
          )
      );
   }

   public static String genId() {
      return "[I;" + CodeUtilities.rng.nextInt() + "," + CodeUtilities.rng.nextInt() + ","
          + CodeUtilities.rng.nextInt() + "," + CodeUtilities.rng.nextInt() + "]";
   }
}
