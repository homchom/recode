package io.github.homchom.recode.mod.commands.impl.text;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.commands.ColorsMenu;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.*;

public class ColorsCommand extends Command {

    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        cd.register(ArgBuilder.literal("colors").executes((ctx) -> {
            if (Config.getBoolean("colorReplacePicker")) {
                showColorPalette(1);
            } else {
                ColorsMenu colorsGui = new ColorsMenu();
                colorsGui.scheduleOpenGui(colorsGui, "");
                //showColorPalette(1);
            }
            return 1;
        })
                .then(ArgBuilder.argument("Saturation(%)", IntegerArgumentType.integer(0, 100)).executes((ctx) -> {
                    float saturation = (float) IntegerArgumentType.getInteger(ctx, "Saturation(%)");
                    showColorPalette(saturation / 100f);
                    return 1;
                })));
    }

    @Override
    public String getDescription() {
        return "[blue]/colors [saturation][reset]\n"
            + "\n"
            + "Shows the color picker menu.\n"
            + "When Saturation value is specified, it will show you the old color picker in chat.";
    }

    @Override
    public String getName() {
        return "/colors";
    }

    private void showColorPalette(float saturation) {
        int maxColors = Config.getInteger("colorMaxRender");
        int lines = Config.getInteger("colorLines");

        for (int j = 0; j < lines; j++) {
            Component base = Component.literal("");
            float b = 1f - ((1f / lines) * j);
            for (int i = 0; i < maxColors; i++) {
                float index = (360 / maxColors) * i;

                java.awt.Color color = java.awt.Color.getHSBColor(index / 360, saturation, b);
                String colorName = "#" + Integer.toHexString(color.getRGB()).substring(2);

                Style colorStyle = Style.EMPTY.withColor(TextColor.fromRgb(color.getRGB()));
                MutableComponent extra = Component.literal("|");
                MutableComponent hover = Component.literal(colorName);
                hover.append("\n§7Click to copy!");
                extra.setStyle(colorStyle);
                hover.setStyle(colorStyle);
                extra.withStyle((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/color hex " + colorName)));
                extra.withStyle((style) -> style.withHoverEvent(HoverEvent.Action.SHOW_TEXT.deserializeFromLegacy(hover)));
                base.getSiblings().add(extra);
            }
            mc.player.displayClientMessage(base, false);
        }
    }
}