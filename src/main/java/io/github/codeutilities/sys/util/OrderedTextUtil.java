package io.github.codeutilities.sys.util;

import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;

public class OrderedTextUtil implements CharacterVisitor {

    StringBuilder sb = new StringBuilder();

    @Override
    public boolean accept(int index, Style style, int codePoint) {
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
