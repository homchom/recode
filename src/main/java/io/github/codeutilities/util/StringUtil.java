package io.github.codeutilities.util;

import io.github.codeutilities.CodeUtilities;
import net.minecraft.text.LiteralText;

import java.util.Arrays;

public class StringUtil {

    public static LiteralText of(String... literalTexts) {
        int length = literalTexts.length;
        LiteralText text = new LiteralText(literalTexts[0]);

        if (length == 1) {
            return text;
        }

        for (String currentText : Arrays.copyOfRange(literalTexts, 1, length)) {
            text.append(currentText);
        }
        return text;
    }

    public static String fromTrimmed(String trimmedUUID) throws IllegalArgumentException {
        if (trimmedUUID == null) throw new IllegalArgumentException();
        StringBuilder builder = new StringBuilder(trimmedUUID.trim());
        try {
            builder.insert(20, "-");
            builder.insert(16, "-");
            builder.insert(12, "-");
            builder.insert(8, "-");
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        }
        return builder.toString();
    }

    public static String genDummyIntArray() {
        return "[I;" + CodeUtilities.rng.nextInt() + "," + CodeUtilities.rng.nextInt() + ","
                + CodeUtilities.rng.nextInt() + "," + CodeUtilities.rng.nextInt() + "]";
    }

}
