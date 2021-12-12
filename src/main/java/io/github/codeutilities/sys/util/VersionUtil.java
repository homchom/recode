package io.github.codeutilities.sys.util;

import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.sys.networking.WebUtil;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;

public class VersionUtil {

    public static int getLatestVersion() {
        try {
            String webContent = WebUtil.getString("https://api.github.com/repos/CodeUtilities/CodeUtilities/releases/latest");
            JsonObject jsonObject = CodeUtilities.JSON_PARSER.parse(webContent).getAsJsonObject();
            return Integer.parseInt(jsonObject.get("name").getAsString().substring(6));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getCurrentVersionInt() {
        try {
            return Integer.parseInt(CodeUtilities.MOD_VERSION);
        }catch (NumberFormatException e) {
            return -1;
        }
    }

}
