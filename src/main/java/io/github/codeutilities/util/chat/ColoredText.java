package io.github.codeutilities.util.chat;

import net.minecraft.text.LiteralText;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class ColoredText extends LiteralText {
    public ColoredText(String code, String string) {
        super(string);

        Formatting formatting = Formatting.byCode(code.charAt(0));

        if(formatting == null) {
            this.styled(style -> style.withColor(TextColor.fromRgb(Integer.parseInt(code, 16))));
        }else {
            this.styled(style -> style.withColor(TextColor.fromFormatting(formatting)));
        }
    }

}
