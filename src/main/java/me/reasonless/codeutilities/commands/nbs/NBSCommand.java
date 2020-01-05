package me.reasonless.codeutilities.commands.nbs;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.realmsclient.gui.ChatFormatting;

import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

public class NBSCommand {
	public static int execute(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
		mc.player.sendMessage(new LiteralText(ChatFormatting.GRAY + "" + ChatFormatting.STRIKETHROUGH + "========================================="));
		mc.player.sendMessage(new LiteralText(ChatFormatting.YELLOW + "/nbs load <file>"));
		mc.player.sendMessage(new LiteralText("Generates a code template for the file."));
		mc.player.sendMessage(new LiteralText(" "));
		mc.player.sendMessage(new LiteralText(ChatFormatting.YELLOW + "/nbs player"));
		mc.player.sendMessage(new LiteralText("Gives you the music player that can play templates."));
		mc.player.sendMessage(new LiteralText(ChatFormatting.GRAY + "" + ChatFormatting.STRIKETHROUGH + "========================================="));
		return 1;
	}
}
