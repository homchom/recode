package io.github.homchom.recode.mod.commands.impl.text;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.*;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.*;

import java.awt.*;

public class ColorCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        cd.register(ArgBuilder.literal("color")
                .then(ArgBuilder.literal("rgb")
                        .then(ArgBuilder.argument("r", IntegerArgumentType.integer(0, 255))
                                .then(ArgBuilder.argument("g", IntegerArgumentType.integer(0, 255)).
                                        then(ArgBuilder.argument("b", IntegerArgumentType.integer(0, 255)).executes((ctx) -> {

                                            int r = ctx.getArgument("r", Integer.class);
                                            int g = ctx.getArgument("g", Integer.class);
                                            int b = ctx.getArgument("b", Integer.class);

                                            copyColor(new Color(r, g, b));
                                            return 1;

                                        })))
                        ))
                .then(ArgBuilder.literal("hex")
                        .then(ArgBuilder.argument("color", StringArgumentType.greedyString()).executes((ctx) -> {
                            String color = ctx.getArgument("color", String.class);
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
                                        then(ArgBuilder.argument("b", IntegerArgumentType.integer(0, 360)).executes((ctx) -> {

                                            float h = ctx.getArgument("h", Integer.class) / 360.0f;
                                            float s = ctx.getArgument("s", Integer.class) / 360.0f;
                                            float b = ctx.getArgument("b", Integer.class) / 360.0f;

                                            copyColor(Color.getHSBColor(h, s, b));
                                            return 1;
                                        })))
                        )));

    }

    @Override
    public String getDescription() {
        return "[blue]/color rgb <r> <g> <b>[reset]\n"
            + "[blue]/color hex <hex>[reset]\n"
            + "[blue]/color hsb <h> <s> <b>[reset]\n"
            + "\n"
            + "Copies the specified color in DiamondFire hex color format.\n"
            + "The max number is [green]256[reset] for RGB colors, and [green]360[reset] for HSB colors.\n"
            + "[yellow]Example[reset]: /color 255 0 0 -> &x&f&f&0&0&0&0";
    }

    @Override
    public String getName() {
        return "/color";
    }

    private void copyColor(Color color) {
        String colorName = Integer.toHexString(color.getRGB()).substring(2);

        String colorNameReal = "#" + Integer.toHexString(color.getRGB()).substring(2);
        Style colorStyle = Style.EMPTY.withColor(TextColor.fromRgb(color.getRGB()));

        MutableComponent text = Component.literal("Copied Color! ");
        MutableComponent preview = Component.literal("█");
        MutableComponent hover = Component.literal(colorNameReal);
        hover.append("\n§7Click to copy!");
        hover.setStyle(colorStyle);
        preview.withStyle((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/color hex " + colorNameReal)));
        preview.withStyle((style) -> style.withHoverEvent(HoverEvent.Action.SHOW_TEXT.deserializeFromLegacy(hover)));

        Minecraft.getInstance().keyboardHandler.setClipboard("&x&" + String.join("&", colorName.split("")));
        ChatUtil.sendMessage(text.append(ChatUtil.setColor(preview, color)), ChatType.INFO_BLUE);
    }
}
