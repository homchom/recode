package io.github.codeutilities.util.chat;

import net.minecraft.text.LiteralText;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class ColoredText extends LiteralText {
    public ColoredText(TextColor textColor, String string) {
        super(string);
        this.styled(style -> style.withColor(textColor));
    }

    public ColoredText(Formatting formatting, String string) {
        this(TextColor.fromFormatting(formatting), string);
    }

    public ColoredText(int color, String string) {
        this(TextColor.fromRgb(color), string);
    }

    public ColoredText(String hex, String string) {
        this(TextColor.fromRgb(Integer.parseInt(hex, 16)), string);
    }
}
