package io.github.homchom.recode.sys.player.chat.color;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;

import java.util.*;

// TODO remove?
public class ColoredText extends TextComponent {
    public ColoredText(String code, String string) {
        super(string);

        ChatFormatting formatting = ChatFormatting.getByCode(code.charAt(0));

        if (formatting == null) {
            this.withStyle(style -> style.withColor(TextColor.fromRgb(Integer.parseInt(code, 16))));
        }else {
            this.withStyle(style -> style.withColor(TextColor.fromLegacyFormat(formatting)));
        }
    }

    public static MutableComponent multiple(String... str) {
        MutableComponent mutableText = new TextComponent("");
        Iterator<String> stringIterator = Arrays.stream(str).iterator();

        while(stringIterator.hasNext()) {
            String code = stringIterator.next();
            String string = stringIterator.next();

            mutableText.append(new ColoredText(code, string));
        }

        return mutableText;
    }

}
