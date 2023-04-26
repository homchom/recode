package io.github.homchom.recode.mod.commands.impl.text;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import io.github.homchom.recode.sys.player.chat.color.HSLColor;
import io.github.homchom.recode.sys.player.chat.color.MinecraftColors;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.*;

import java.awt.*;

public class GradientCommand extends Command {
    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        cd.register(ArgBuilder.literal("gradient")
                .then(ArgBuilder.argument("startColor", StringArgumentType.string())
                        .then(ArgBuilder.argument("endColor", StringArgumentType.string())
                                .then(ArgBuilder.argument("text", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String text = ctx.getArgument("text", String.class);
                                            String strStartColor = ctx.getArgument("startColor", String.class);
                                            String strEndColor = ctx.getArgument("endColor", String.class);

                                            HSLColor startColor;
                                            HSLColor endColor;

                                            try {
                                                if (strStartColor.startsWith("#")) {
                                                    startColor = new HSLColor(Color.decode(strStartColor.toLowerCase()));
                                                } else if (strStartColor.matches("^[0-9a-fA-F]*")) {
                                                    startColor = new HSLColor(Color.decode("#" + strStartColor.toLowerCase()));
                                                } else if (strStartColor.startsWith("&x")) {
                                                    startColor = new HSLColor(Color.decode(strStartColor.toLowerCase().replaceAll("&", "").replaceAll("x", "#")));
                                                } else if (strStartColor.matches("^&[0-9a-fA-F]")) {
                                                    char[] chars = strStartColor.toLowerCase().replaceAll("&", "").toCharArray();
                                                    MinecraftColors mcColor = MinecraftColors.fromCode(chars[0]);
                                                    startColor = new HSLColor(mcColor.getColor());
                                                } else {
                                                    ChatUtil.sendMessage("Invalid color!", ChatType.FAIL);
                                                    return -1;
                                                }

                                                if (strEndColor.startsWith("#")) {
                                                    endColor = new HSLColor(Color.decode(strEndColor.toLowerCase()));
                                                } else if (strEndColor.matches("^[0-9a-fA-F]*")) {
                                                    endColor = new HSLColor(Color.decode("#" + strEndColor.toLowerCase()));
                                                } else if (strEndColor.startsWith("&x")) {
                                                    endColor = new HSLColor(Color.decode(strEndColor.toLowerCase().replaceAll("&", "").replaceAll("x", "#")));
                                                } else if (strEndColor.matches("^&[0-9a-fA-F]")) {
                                                    char[] chars = strEndColor.toLowerCase().replaceAll("&", "").toCharArray();
                                                    MinecraftColors mcColor = MinecraftColors.fromCode(chars[0]);
                                                    endColor = new HSLColor(mcColor.getColor());
                                                } else {
                                                    ChatUtil.sendMessage("Invalid color!", ChatType.FAIL);
                                                    return -1;
                                                }
                                            } catch (Exception e) {
                                                ChatUtil.sendMessage("Invalid color!", ChatType.FAIL);
                                                return -1;
                                            }

                                            StringBuilder sb = new StringBuilder();
                                            char[] chars = text.toCharArray();
                                            float i = 0;
                                            String lastHex = "";
                                            int spaces = 0;

                                            MutableComponent base = Component.literal("§a→ §r");

                                            for (char c : chars) {
                                                if (c == ' ') {
                                                    spaces++;
                                                    base.getSiblings().add(Component.literal(" "));
                                                    sb.append(c);
                                                    continue;
                                                }
                                                HSLColor temp = new HSLColor((float) lerp(startColor.getHue(), endColor.getHue(), i / (float) (text.length() - 1 - spaces)),
                                                        (float) lerp(startColor.getSaturation(), endColor.getSaturation(), i / (float) (text.length() - 1 - spaces)),
                                                        (float) lerp(startColor.getLuminance(), endColor.getLuminance(), i / (float) (text.length() - 1 - spaces)));
                                                Color temp2 = HSLColor.toRGB(temp.getHue(), temp.getSaturation(), temp.getLuminance());
                                                String hex = String.format("%02x%02x%02x", temp2.getRed(), temp2.getGreen(), temp2.getBlue());

                                                if (!lastHex.equals(hex)) {
                                                    lastHex = hex;
                                                    String dfHex = "&x&" + String.join("&", hex.split(""));
                                                    sb.append(dfHex);
                                                }

                                                Style colorStyle = Style.EMPTY.withColor(TextColor.fromRgb(temp2.getRGB()));
                                                String colorName = "#" + hex;
                                                MutableComponent extra = Component.literal(String.valueOf(c));
                                                MutableComponent hover = Component.literal(colorName);
                                                hover.append("\n§7Click to copy!");
                                                extra.setStyle(colorStyle);
                                                hover.setStyle(colorStyle);
                                                extra.withStyle((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/color hex " + colorName)));
                                                extra.withStyle((style) -> style.withHoverEvent(HoverEvent.Action.SHOW_TEXT.deserializeFromLegacy(hover)));
                                                base.getSiblings().add(extra);

                                                sb.append(c);
                                                i++;
                                            }
                                            Minecraft.getInstance().keyboardHandler.setClipboard(sb.toString());
                                            ChatUtil.sendMessage("Copied text!", ChatType.SUCCESS);
                                            mc.player.displayClientMessage(base, false);

                                            if (DFInfo.currentState.getMode() == LegacyState.Mode.DEV) {
                                                mc.player.connection.sendUnsignedCommand("txt " + sb);
                                            }

                                            return 1;
                                        })
                                )
                        )
                )
        );
    }

    @Override
    public String getDescription() {
        return "[blue]/gradient <start> <end> <text>[reset]\n"
            + "\n"
            + "Generates a text with a color gradient starting at the hex color 'start' and ending at 'end'\n"
            + "[yellow]Example[reset]: /gradient #ff0000 #00ff00 Something -> INSERT HERE";
    }

    @Override
    public String getName() {
        return "/gradient";
    }

    private double lerp(float x, float y, float p) {
        return x + (y - x) * p;
    }
}
