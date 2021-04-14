package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.config.ModConfig;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;

public class ColorsCommand extends Command {

    private MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("colors").executes((context) -> {
            showColorPalette(1);
            return 1;
        })
                .then(ArgBuilder.argument("Saturation(%)", IntegerArgumentType.integer(0, 100)).executes((context) -> {
                    float saturation = (float)IntegerArgumentType.getInteger(context, "Saturation(%)");
                    showColorPalette(saturation/100f);
                    return 1;
                })));
    }

    private void showColorPalette(float saturation) {
        int maxColors = ModConfig.getConfig(ModConfig.Commands_Color.class).colorMaxRender;
        int lines = ModConfig.getConfig(ModConfig.Commands_Color.class).colorLines;

        for (int j = 0; j < lines; j++) {
            Text base = new LiteralText("");
            float b = 1f - ((1f / lines) * j);
            for (int i = 0; i < maxColors; i++) {
                float index = (360 / maxColors) * i;

                java.awt.Color color = java.awt.Color.getHSBColor(index / 360, saturation, b);
                String colorName = "#" + Integer.toHexString(color.getRGB()).substring(2);

                Style colorStyle = Style.EMPTY.withColor(TextColor.fromRgb(color.getRGB()));
                LiteralText extra = new LiteralText("|");
                LiteralText hover = new LiteralText(colorName);
                hover.append("\n§7Click to copy!");
                extra.setStyle(colorStyle);
                hover.setStyle(colorStyle);
                extra.styled((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/color hex " + colorName)));
                extra.styled((style) -> style.withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(hover)));
                base.getSiblings().add(extra);
            }
            mc.player.sendMessage(base, false);
        }
    }
}