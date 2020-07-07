package me.reasonless.codeutilities.commands.nbs;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.reasonless.codeutilities.nbs.NBSDecoder;
import me.reasonless.codeutilities.nbs.NBSToTemplate;
import me.reasonless.codeutilities.nbs.SongData;
import me.reasonless.codeutilities.util.MinecraftColors;
import me.reasonless.codeutilities.util.TemplateNBT;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

import java.io.File;

public class NBSLoadCommand {
	public static int execute(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) throws Exception {
		File f = new File("CodeUtilities/NBS Files/" + StringArgumentType.getString(ctx, "location") + (StringArgumentType.getString(ctx, "location").endsWith(".nbs") ? "" : ".nbs"));
		if(f.exists()) {
			loadNbs(f);
		}else {
			File f2 = new File("nbsFiles/" + StringArgumentType.getString(ctx, "location") + (StringArgumentType.getString(ctx, "location").endsWith(".nbs") ? "" : ".nbs"));
			if(f2.exists()) {
				loadNbs(f2);
			}else {
				mc.player.sendMessage(new LiteralText(MinecraftColors.DARK_RED + " - " + MinecraftColors.RED + "That nbs file doesn't exist."), false);
			}	
		}
		return 1;
	}
	
	public static void loadNbs(File file) throws Exception {
		
		MinecraftClient mc = MinecraftClient.getInstance();
		
		mc.player.sendMessage(new LiteralText(MinecraftColors.DARK_GREEN + " - " + MinecraftColors.GREEN + "Loading a nbs file, this may take long..."), false);
		SongData d = NBSDecoder.parse(file);
		String code = new NBSToTemplate(d).convert();
		ItemStack stack = new ItemStack(Items.NOTE_BLOCK);
		TemplateNBT.setTemplateNBT(stack, d.getName(), d.getAuthor(), code);
		
		if(d.getName().length() == 0) {
			String name;
			if (d.getFileName().indexOf(".") > 0) {
				name = d.getFileName().substring(0, d.getFileName().lastIndexOf("."));
			}else {
				name = d.getFileName();
			}
			stack.setCustomName(new LiteralText(MinecraftColors.DARK_PURPLE + "SONG " + MinecraftColors.GRAY + " - " + MinecraftColors.WHITE + " " + name));
		}else {
			stack.setCustomName(new LiteralText(MinecraftColors.DARK_PURPLE + "SONG " + MinecraftColors.GRAY + " - " + MinecraftColors.WHITE + " " + d.getName()));
		}

		mc.interactionManager.clickCreativeStack(stack, 36 + mc.player.inventory.selectedSlot);

		mc.player.sendMessage(new LiteralText(MinecraftColors.GOLD + " - " + MinecraftColors.YELLOW + "Loaded nbs! Check your inventory."), false);
		mc.player.sendMessage(new LiteralText(MinecraftColors.DARK_AQUA + " - " + MinecraftColors.AQUA + "If you need a nbs player do /nbs player to get one!"), false);
	}
}
