package io.github.homchom.recode.mod.features.modules.translations;

import io.github.homchom.recode.Recode;
import io.github.homchom.recode.sys.file.FileUtil;
import org.json.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.HashMap;

public class Translation {

    private static final HashMap<String, String> TRANSLATIONS = new HashMap<>();
    private static final HashMap<String, String> CLIENT_TRANSLATIONS = new HashMap<>();

    public static String get(String key) {
        return TRANSLATIONS.getOrDefault(key, key);
    }

    public static String get(String moduleId, String key) {
        return get("module." + moduleId + "." + key);
    }

    public static void put(String key, String value) {
        TRANSLATIONS.put(key, value);
    }

    public static String getExternal(String key) {
        String value = CLIENT_TRANSLATIONS.get(key);

        if (value == null) {
            // get the value if it's not set already
            // get json object of language
            Path path = Paths.get("").toAbsolutePath().getParent().resolve("src/main/resources/assets/recode/lang/" + Recode.CLIENT_LANG + ".json");
            Path fallbackPath = Paths.get("").toAbsolutePath().getParent().resolve("src/main/resources/assets/recode/lang/en_us.json");
            if (!path.toFile().exists()) path = fallbackPath;

            // get client lang json
            String jsonString = "";
            try { jsonString = FileUtil.readFile(String.valueOf(path), Charset.defaultCharset());
            } catch (IOException e) { e.printStackTrace(); }

            JSONObject json = new JSONObject();
            try { json = new JSONObject(jsonString);
            } catch (JSONException e) { e.printStackTrace(); }

            // get fallback lang json
            jsonString = "";
            try { jsonString = FileUtil.readFile(String.valueOf(fallbackPath), Charset.defaultCharset());
            } catch (IOException e) { e.printStackTrace(); }

            JSONObject fallbackJson = new JSONObject();
            try { fallbackJson = new JSONObject(jsonString);
            } catch (JSONException e) { e.printStackTrace(); }

            // get value from client lang or fallback lang json
            if (json.has(key)) {
                value = json.getString(key);
            } else {
                if (fallbackJson.has(key)) {
                    value = fallbackJson.getString(key);
                } else value = key;
            }

            // cache and return result
            CLIENT_TRANSLATIONS.put(key, value);

        }
        return value;
    }

}
