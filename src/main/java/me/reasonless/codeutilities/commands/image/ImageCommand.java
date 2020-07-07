package me.reasonless.codeutilities.commands.image;

import com.mojang.brigadier.context.CommandContext;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.reasonless.codeutilities.util.MinecraftColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

public class ImageCommand {
    public static int execute(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
        mc.player.sendMessage(new LiteralText(MinecraftColors.GRAY + "" + MinecraftColors.STRIKETHROUGH + "========================================="), false);
        mc.player.sendMessage(new LiteralText(MinecraftColors.YELLOW + "/image load <file>"), false);
        mc.player.sendMessage(new LiteralText("Generates a code template which creates a"), false);
        mc.player.sendMessage(new LiteralText("hologram of the file."), false);
        mc.player.sendMessage(new LiteralText(MinecraftColors.GRAY + "" + MinecraftColors.STRIKETHROUGH + "========================================="), false);
        return 1;
    }
}
