package me.reasonless.codeutilities.commands.image;

import com.mojang.brigadier.context.CommandContext;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.reasonless.codeutilities.util.MinecraftColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

public class ImageCommand {
    public static int execute(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
        mc.player.sendMessage(new LiteralText(MinecraftColors.GRAY + "" + MinecraftColors.STRIKETHROUGH + "========================================="));
        mc.player.sendMessage(new LiteralText(MinecraftColors.YELLOW + "/image load <file>"));
        mc.player.sendMessage(new LiteralText("Generates a code template which creates a"));
        mc.player.sendMessage(new LiteralText("hologram of the file."));
        mc.player.sendMessage(new LiteralText(MinecraftColors.GRAY + "" + MinecraftColors.STRIKETHROUGH + "========================================="));
        return 1;
    }
}
