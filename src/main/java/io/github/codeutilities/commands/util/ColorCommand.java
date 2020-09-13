package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.util.*;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.awt.*;
import java.util.Map;

public class ColorCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("color")
                .then(ArgBuilder.literal("rgb")
                        .then(ArgBuilder.argument("r", IntegerArgumentType.integer(0, 255))
                                .then(ArgBuilder.argument("g", IntegerArgumentType.integer(0, 255)).
                                        then(ArgBuilder.argument("b", IntegerArgumentType.integer(0, 255)).executes((context) -> {

                                            int r = context.getArgument("r", Integer.class);
                                            int g = context.getArgument("g", Integer.class);
                                            int b = context.getArgument("b", Integer.class);

                                            copyColor(new Color(r, g, b));
                                            return 1;

                                        })))
                        ))
                .then(ArgBuilder.literal("hex")
                        .then(ArgBuilder.argument("color", StringArgumentType.greedyString()).executes((context) -> {
                            String color = context.getArgument("color", String.class);
                            Color hex;
                            try {
                                hex = Color.decode(color);
                            } catch (NumberFormatException e) {
                                ChatUtil.sendMessage("Invalid Hex!", ChatType.FAIL);
                                return -1;
                            }
                            copyColor(hex);
                            return 1;
                        })))
                .then(ArgBuilder.literal("hsb")
                        .then(ArgBuilder.argument("h", IntegerArgumentType.integer(0, 360))
                                .then(ArgBuilder.argument("s", IntegerArgumentType.integer(0, 360)).
                                        then(ArgBuilder.argument("b", IntegerArgumentType.integer(0, 360)).executes((context) -> {

                                            float h = context.getArgument("h", Integer.class) / 360;
                                            float s = context.getArgument("s", Integer.class) / 360;
                                            float b = context.getArgument("b", Integer.class) / 360;

                                            copyColor(Color.getHSBColor(h, s, b));
                                            return 1;
                                        })))
                        )));
    }

    private void copyColor(Color color) {
        String colorName = Integer.toHexString(color.getRGB()).substring(2);
        LiteralText text = new LiteralText("Copied Color! ");
        LiteralText preview = new LiteralText("█");

        MinecraftClient.getInstance().keyboard.setClipboard("&x&" + String.join("&", colorName.split("")));
        ChatUtil.sendMessage(text.append(ChatUtil.setColor(preview, color)), ChatType.INFO_BLUE);
    }
}
