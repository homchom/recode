package io.github.homchom.recode.sys.util;

import com.google.gson.*;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.sys.networking.WebUtil;

import java.io.IOException;

public class VersionUtil {

    public static int getLatestVersion() {
        try {
            String webContent = WebUtil.getString("https://api.github.com/repos/homchom/recode/releases/latest");
            JsonObject jsonObject = JsonParser.parseString(webContent).getAsJsonObject();
            return Integer.parseInt(jsonObject.get("name").getAsString().substring(6));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getCurrentVersionInt() {
        try {
            return Integer.parseInt(LegacyRecode.getVersion());
        }catch (NumberFormatException e) {
            return -1;
        }
    }

}
