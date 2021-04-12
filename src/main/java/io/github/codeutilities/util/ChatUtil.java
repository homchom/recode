package io.github.codeutilities.util;

import io.github.codeutilities.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.*;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class ChatUtil {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void sendMessage(String text) {
        sendMessage(new LiteralText(text), null);
    }

    public static void sendMessage(String text, ChatType prefixType) {
        sendMessage(new LiteralText(text), prefixType);
    }

    public static void sendTranslateMessage(String identifier, ChatType prefixType) {
        sendMessage(new TranslatableText(identifier), prefixType);
    }

    public static void sendMessage(TranslatableText component, ChatType prefixType) {
        sendMessage((BaseText) component, prefixType);
    }

    public static void sendMessage(MutableText text, @Nullable ChatType chatType) {
        if (chatType == null) {
            mc.player.sendMessage(text, false);
        } else {
            ChatUtil.setColor(text, MinecraftColors.fromCode(chatType.getTrailing()).getColor());
            mc.player.sendMessage(new LiteralText(chatType.getString() + " ").append(text), false);
            if (chatType == ChatType.FAIL) {
                if (ModConfig.getConfig().errorSound) {
                    MinecraftClient.getInstance().player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 2, 0);
                }
            }
        }
    }

    public static MutableText setColor(MutableText component, Color color) {
        Style colorStyle = component.getStyle().withColor(TextColor.fromRgb(color.getRGB()));
        component.setStyle(colorStyle);
        return component;
    }

    public static String textComponentToColorCodes(Text message) {
        List<Text> siblings = message.getSiblings();
        int siblingsAmount = siblings.size();

        StringBuilder newMsg = new StringBuilder();
        String currentText = "";

        for (int i = 0; i < siblingsAmount; i++) {
            Text sibling = siblings.get(i);
            Style style = sibling.getStyle();

            // color
            TextColor color = style.getColor();
            String literalColor = String.valueOf(color);
            String code = MinecraftColors.getMcFromFormatting(color);
            if (code == null) currentText = MinecraftColors.hexToMc(literalColor);
            else currentText = code;

            if (style.isBold()) currentText = currentText + "§l";
            if (style.isItalic()) currentText = currentText + "§o";
            if (style.isStrikethrough()) currentText = currentText + "§m";
            if (style.isUnderlined()) currentText = currentText + "§n";
            if (style.isObfuscated()) currentText = currentText + "§k";

            currentText = currentText + sibling.getString();
            newMsg.append(currentText);
        }

        return newMsg.toString();
    }

}
