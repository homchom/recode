package io.github.homchom.recode.mod.features.commands.schem.utils;

import java.util.*;
import java.util.stream.IntStream;

public class StringUtils {
    //Code taken from https://stackoverflow.com/questions/43057690/java-stream-collect-every-n-elements/47112162
    public static String[] JoinString(int iterations, CharSequence delimiter, String[] elements) {
        List<String> list = Arrays.asList(elements);
        return IntStream.range(0, (list.size() + iterations - 1) / iterations)
                .mapToObj(i -> String.join(delimiter, list.subList(i * iterations, Math.min(iterations * (i + 1), list.size()))))
                .toArray(String[]::new);
    }
}
