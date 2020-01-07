package me.reasonless.codeutilities.commands.help;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.realmsclient.gui.ChatFormatting;

import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.reasonless.codeutilities.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

public class HelpCommand {
	
	static String version = Main.MOD_VERSION;
	
	public static int execute(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
		mc.player.sendMessage(new LiteralText(ChatFormatting.GRAY + "" + ChatFormatting.STRIKETHROUGH + "========================================="));
		mc.player.sendMessage(new LiteralText(ChatFormatting.DARK_AQUA + "" + ChatFormatting.BOLD + "Code" + ChatFormatting.AQUA + "" + ChatFormatting.BOLD + "Utilities" + ChatFormatting.GRAY + " - " + ChatFormatting.RESET + "v" + version));
		mc.player.sendMessage(new LiteralText(" "));
		mc.player.sendMessage(new LiteralText(ChatFormatting.YELLOW + "/codeutilities music"));
        mc.player.sendMessage(new LiteralText("Shows all music commands"));
        mc.player.sendMessage(new LiteralText(" "));
        mc.player.sendMessage(new LiteralText(ChatFormatting.YELLOW + "/codeutilities image"));
        mc.player.sendMessage(new LiteralText("Shows all image commands"));
		mc.player.sendMessage(new LiteralText(ChatFormatting.GRAY + "" + ChatFormatting.STRIKETHROUGH + "========================================="));
		return 1;
	}
}
