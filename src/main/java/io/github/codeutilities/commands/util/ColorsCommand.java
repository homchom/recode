package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorsCommand extends Command {

    //private final ModConfig config = ModConfig.getConfig();
    public static final Map<String, Float> COLOR_MAP = new HashMap<String, Float>() {{
        put("red", 0F);
        put("orange", 30F);
        put("yellow", 60F);
        put("green", 120F);
        put("aqua", 180F);
        put("blue", 210F);
        put("indigo", 240F);
        put("purple", 270F);
        put("magenta", 300F);
    }};

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("colors").executes((context) -> {
            this.generatePicker(mc, 0);
            return 1;
        })
                .then(ArgBuilder.argument("saturation", IntegerArgumentType.integer(0, 100)).executes((context) -> {
                    float saturation = (float) IntegerArgumentType.getInteger(context, "saturation");
                    this.generatePicker(mc, saturation);
                    return 1;
                }))
                .then(ArgBuilder.argument("color", StringArgumentType.string()).executes((context) -> {
                    String string = StringArgumentType.getString(context, "color");
                    if (COLOR_MAP.containsKey(string)) {
                        this.generatePicker(mc, COLOR_MAP.get(string));
                    } else {
                        ChatUtil.sendMessage("Could not find this color, try again!", ChatType.FAIL);
                    }
                    return 1;
                })));
    }

    private void generatePicker(MinecraftClient mc, float hue) {
        List<LiteralText> textComponents = generateColorPicker(hue);
        Text finalText = new LiteralText("");
        LiteralText nextLine = new LiteralText("\n");

        int i = -1;
        for (LiteralText text : textComponents) {
            i += 1;

            if (i % 11 == 0 && i != 0) {
                finalText.getSiblings().add(nextLine);
            }

            finalText.getSiblings().add(text);
        }

        ChatUtil.sendMessage("Click a color to copy to your clipboard!", ChatType.INFO_BLUE);
        this.sendMessage(mc, finalText);
    }

    public static List<LiteralText> generateColorPicker(float hue) {
        return generateColorPicker(hue, "█");
    }

    public static List<LiteralText> generateColorPicker(float hue, String message) {
        List<LiteralText> list = new ArrayList<>();
        hue = hue / 360;
        String paste = "§6⧈ §eClick to copy the hex color! §b";

        for (int i = 0; i <= 10; i++) {
            for (int j = 0; j <= 10; j++) {

                Color color = Color.getHSBColor(hue, j / 10f, (10 - i) / 10f);
                String hex = "#" + Integer.toHexString(color.getRGB()).substring(2);

                Style colorStyle = Style.EMPTY.withColor(TextColor.fromRgb(color.getRGB()));
                LiteralText extra = new LiteralText(message);
                LiteralText hover = new LiteralText(hex);
                hover.append("\n" + paste);
                extra.setStyle(colorStyle);
                hover.setStyle(colorStyle);
                extra.styled((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/color hex " + hex)));
                extra.styled((style) -> style.withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(hover)));
                list.add(extra);
            }
        }

        return list;
    }
}
