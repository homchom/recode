package me.reasonless.codeutilities.commands.image;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.realmsclient.gui.ChatFormatting;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

public class ImageCommand {
    public static int execute(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
        mc.player.sendMessage(new LiteralText(ChatFormatting.GRAY + "" + ChatFormatting.STRIKETHROUGH + "========================================="));
        mc.player.sendMessage(new LiteralText(ChatFormatting.YELLOW + "/image load <file>"));
        mc.player.sendMessage(new LiteralText("Generates a code template which creates a"));
        mc.player.sendMessage(new LiteralText("hologram of the file."));
        mc.player.sendMessage(new LiteralText(ChatFormatting.GRAY + "" + ChatFormatting.STRIKETHROUGH + "========================================="));
        return 1;
    }
}
