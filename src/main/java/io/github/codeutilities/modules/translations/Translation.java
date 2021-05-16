package io.github.codeutilities.modules.translations;

import java.util.HashMap;

public class Translation {

    private static HashMap<String, String> TRANSLATIONS = new HashMap<>();

    public static String get(String key) {
        return TRANSLATIONS.get(key);
    }

}
