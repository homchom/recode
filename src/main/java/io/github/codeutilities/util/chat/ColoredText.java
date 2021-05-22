package io.github.codeutilities.util.chat;

import net.minecraft.text.LiteralText;
import net.minecraft.text.TextColor;

public class ColoredText extends LiteralText {
    public ColoredText(int color, String string) {
        super(string);
        this.styled(style -> style.withColor(TextColor.fromRgb(color)));
    }

    public ColoredText(String hex, String string) {
        this(Integer.parseInt(hex, 16), string);
    }
}
