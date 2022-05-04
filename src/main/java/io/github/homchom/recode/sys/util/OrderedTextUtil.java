package io.github.homchom.recode.sys.util;

import io.github.homchom.recode.sys.player.chat.color.MinecraftColors;
import net.minecraft.network.chat.*;
import net.minecraft.util.*;

public class OrderedTextUtil implements FormattedCharSink {

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

    public static String getString(FormattedCharSequence o) {
        OrderedTextUtil u = new OrderedTextUtil();
        o.accept(u);
        return u.getString();
    }
}
