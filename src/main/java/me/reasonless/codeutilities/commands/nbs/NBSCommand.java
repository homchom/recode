package me.reasonless.codeutilities.commands.nbs;

import com.mojang.brigadier.context.CommandContext;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.reasonless.codeutilities.util.MinecraftColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

public class NBSCommand {
	public static int execute(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
		mc.player.sendMessage(new LiteralText(MinecraftColors.GRAY + "" + MinecraftColors.STRIKETHROUGH + "========================================="), false);
		mc.player.sendMessage(new LiteralText(MinecraftColors.YELLOW + "/nbs load <file>"), false);
		mc.player.sendMessage(new LiteralText("Generates a code template for the file."), false);
		mc.player.sendMessage(new LiteralText(" "), false);
		mc.player.sendMessage(new LiteralText(MinecraftColors.YELLOW + "/nbs player"), false);
		mc.player.sendMessage(new LiteralText("Gives you the music player that can play templates."), false);
		mc.player.sendMessage(new LiteralText(MinecraftColors.GRAY + "" + MinecraftColors.STRIKETHROUGH + "========================================="), false);
		return 1;
	}
}
