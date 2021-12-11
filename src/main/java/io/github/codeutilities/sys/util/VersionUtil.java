package io.github.codeutilities.sys.util;

import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.sys.networking.WebUtil;

import java.io.IOException;

public class VersionUtil {

    public static String getLatestVersion() {
        try {
            String webContent = WebUtil.getString("https://api.github.com/repos/CodeUtilities/CodeUtilities/releases/latest");
            JsonObject jsonObject = CodeUtilities.JSON_PARSER.parse(webContent).getAsJsonObject();
            return jsonObject.get("name").getAsString().substring(6);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
