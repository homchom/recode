package io.github.homchom.recode.mod.commands.arguments;

import com.mojang.brigadier.StringReader;

public class StringReaders {
    public static String readRemaining(StringReader reader) {
        final String text = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        return text;
    }
}
