package io.github.codeutilities.sys.util;

import io.github.codeutilities.sys.player.chat.color.MinecraftColors;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;

public class OrderedTextUtil implements CharacterVisitor {

    StringBuilder sb = new StringBuilder();

    @Override
    public boolean accept(int index, Style style, int codePoint) {
        TextColor color = style.getColor();
        String code = MinecraftColors.getMcFromFormatting(color);
        if (code == null) {
            sb.append(MinecraftColors.hexToMc(String.valueOf(color)));
        } else {
            sb.append(code);
        }
        if (style.isBold()) sb.append("§l");
        if (style.isItalic()) sb.append("§o");
        if (style.isStrikethrough()) sb.append("§n");
        if (style.isUnderlined()) sb.append("§m");
        if (style.isBold()) sb.append("§k");
        sb.appendCodePoint(codePoint);
        return true;
    }

    public String getString() {
        return sb.toString();
    }

    public static String getString(OrderedText o) {
        OrderedTextUtil u = new OrderedTextUtil();
        o.accept(u);
        return u.getString();
    }
}
