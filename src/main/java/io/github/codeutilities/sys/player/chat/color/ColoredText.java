package io.github.codeutilities.sys.player.chat.color;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.Iterator;

// TODO remove?
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

    public static MutableText multiple(String... str) {
        MutableText mutableText = new LiteralText("");
        Iterator<String> stringIterator = Arrays.stream(str).iterator();

        while(stringIterator.hasNext()) {
            String code = stringIterator.next();
            String string = stringIterator.next();

            mutableText.append(new ColoredText(code, string));
        }

        return mutableText;
    }

}
