package me.reasonless.codeutilities.commands.nbs;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.realmsclient.gui.ChatFormatting;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.reasonless.codeutilities.nbs.NBSDecoder;
import me.reasonless.codeutilities.nbs.NBSToTemplate;
import me.reasonless.codeutilities.nbs.SongData;
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
			SongData d = NBSDecoder.parse(f);
			String code = new NBSToTemplate(d).convert();
			ItemStack stack = new ItemStack(Items.NOTE_BLOCK);
			TemplateNBT.setTemplateNBT(stack, d.getName(), d.getAuthor(), code);
			
			if(d.getName().length() == 0) {
				stack.setCustomName(new LiteralText(ChatFormatting.DARK_PURPLE + "SONG " + ChatFormatting.GRAY + " - " + ChatFormatting.WHITE + " " + d.getFileName()));
			}else {
				stack.setCustomName(new LiteralText(ChatFormatting.DARK_PURPLE + "SONG " + ChatFormatting.GRAY + " - " + ChatFormatting.WHITE + " " + d.getName()));
			}

			mc.interactionManager.clickCreativeStack(stack, 36 + mc.player.inventory.selectedSlot);

			mc.player.sendMessage(new LiteralText(ChatFormatting.GOLD + " - " + ChatFormatting.YELLOW + "Loaded nbs! Check your inventory."));
			mc.player.sendMessage(new LiteralText(ChatFormatting.DARK_AQUA + " - " + ChatFormatting.AQUA + "If you need a nbs player do /nbs player to get one!"));
		}else {
			mc.player.sendMessage(new LiteralText(ChatFormatting.DARK_RED + " - " + ChatFormatting.RED + "That nbs file doesn't exist."));
		}
		return 1;
	}
}
