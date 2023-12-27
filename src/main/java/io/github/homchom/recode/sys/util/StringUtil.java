package io.github.homchom.recode.sys.util;

import com.google.gson.JsonArray;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Pattern;

public class StringUtil {
    public static final Pattern STRIP_CHARS_PATTERN = Pattern.compile("(^\\s+|\\s+$)");

    public static MutableComponent of(String... literalTexts) {
        int length = literalTexts.length;
        MutableComponent text = Component.literal(literalTexts[0]);

        if (length == 1) {
            return text;
        }

        for (String currentText : Arrays.copyOfRange(literalTexts, 1, length)) {
            text.append(currentText);
        }
        return text;
    }

    public static String fromTrimmed(String trimmedUUID) throws IllegalArgumentException {
        if (trimmedUUID == null) {
            throw new IllegalArgumentException();
        }
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
        var random = kotlin.random.Random.Default;
        return "[I;" + random.nextInt() + "," + random.nextInt() + ","
                + random.nextInt() + "," + random.nextInt() + "]";
    }

    public static String textToString(Component txt) {
        StringBuilder out = new StringBuilder();

        Style s = txt.getStyle();

        TextColor color = s.getColor();

        if (color != null) {
            try {
                ChatFormatting f = ChatFormatting.valueOf(color.serialize().toUpperCase());
                Class<ChatFormatting> cl = ChatFormatting.class;
                Field code = cl.getDeclaredField("code");
                code.setAccessible(true);
                out.append("§").append(code.get(f));
            } catch (Exception err) {
                StringBuilder code = new StringBuilder();
                for (char c : Integer.toHexString(color.getValue()).toUpperCase().toCharArray()) {
                    code.append("§").append(c);
                }
                while (code.length() < 12) {
                    code.insert(0, "§0");
                }

                out.append("§x").append(code);
            }
        }

        if (s.isBold()) {
            out.append("§l");
        }
        if (s.isItalic()) {
            out.append("§o");
        }
        if (s.isStrikethrough()) {
            out.append("§n");
        }
        if (s.isUnderlined()) {
            out.append("§m");
        }
        if (s.isObfuscated()) {
            out.append("§k");
        }

        out.append(txt.getString());

        for (Component sibling : txt.getSiblings()) {
            out.append(textToString(sibling));
        }

        return out.toString();
    }

    public static String[] toStringArray(JsonArray array) {
        if (array == null)
            return null;
        if (array.size() == 0)
            return new String[0];

        String[] arr = new String[array.size()];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = array.get(i).getAsString();
        }
        return arr;
    }

    public static HashSet<String[]> toStringListHashSet(JsonArray array) {
        if (array == null)
            return null;

        HashSet<String[]> arr = new HashSet<>();
        for(int i = 0; i < array.size(); i++) {
            arr.add(toStringArray(array.get(i).getAsJsonArray()));
        }
        return arr;
    }


    //theres probably a 1 method way but i dont feel like doing research
    public static String generateKey(int length) {
        String[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890".split("");
        String returnval = "";
        Random ran = new Random();
        for (int i = 0; i < length; i++) {
            returnval += chars[ran.nextInt(chars.length)];
        }
        return returnval;
    }

}
