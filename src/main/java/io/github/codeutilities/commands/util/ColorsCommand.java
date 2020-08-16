package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.config.ModConfig;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;

public class ColorsCommand extends Command {

    private final ModConfig config = ModConfig.getConfig();

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("colors").executes((context) -> {
            Text base = new LiteralText("");
            int maxColors = config.colorMaxRender;

            for (int i = 0; i < maxColors; i++) {
                float index = (360 / maxColors) * i;
                java.awt.Color color = java.awt.Color.getHSBColor(index / 360, 1, 1);
                String colorName = "#" + Integer.toHexString(color.getRGB()).substring(2);

                Style colorStyle = Style.EMPTY.withColor(TextColor.fromRgb(color.getRGB()));
                LiteralText extra = new LiteralText("|");
                LiteralText hover = new LiteralText(colorName);
                hover.append("\n§7Click to copy!");
                extra.setStyle(colorStyle);
                hover.setStyle(colorStyle);
                extra.styled((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/color " + colorName)));
                extra.styled((style) -> style.withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(hover)));
                base.getSiblings().add(extra);
            }
            mc.player.sendMessage(base, false);

            return 1;
        }));
    }
}
