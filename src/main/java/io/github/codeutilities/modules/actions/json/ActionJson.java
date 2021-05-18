package io.github.codeutilities.modules.actions.json;

import io.github.codeutilities.config.Config;
import io.github.codeutilities.modules.actions.Action;
import io.github.codeutilities.modules.translations.Translation;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionJson extends JSONObject {
    public ActionJson(JSONObject json) {
        super(json.toString());
    }

    private HashMap<String, Object> VARIABLES = new HashMap<>();
    private ModuleJson MODULE;

    @Override
    public String getString(String key) {
        String value = (String) this.get(key);
        if (value.contains("${")) {
            Pattern pattern = Pattern.compile("\\$\\{.+}");
            Matcher matcher = pattern.matcher(value);
            int i = 0;
            while (matcher.find()) {
                String match = matcher.group(i);
                String result = "null";

                Action action = Action.getAction(this.getId());
                String moduleId = MODULE.getId();

                String varname = match.replaceAll("^\\$\\{|}$", "");
                String noprefix = varname.replaceFirst("^[a-z]+.", "");

                if (varname.startsWith("config.")) {
                    result = Config.getString(noprefix);
                } else if (varname.startsWith("translation.")) {
                    result = Translation.get("module."+moduleId+"."+noprefix);
                } else if (varname.startsWith("meta.")) {
                    result = Translation.get("module." + moduleId + ".meta." + noprefix);
                } else if (varname.startsWith("event.")) {
                    result = (String) VARIABLES.get(varname);
                } else if (varname.startsWith("custom.")) {
                    result = (String) VARIABLES.get(varname);
                }

                value = Pattern.compile(match, Pattern.LITERAL).
                        matcher(value).replaceFirst(Matcher.quoteReplacement(result));
                i++;
            }
        }

        return value;
    }

    @Override
    public int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    public void setVars(HashMap<String, Object> variableMap) {
        this.VARIABLES = variableMap;
    }

    public void setModule(ModuleJson module) {
        this.MODULE = module;
    }

    public String getId() {
        return this.getString("action");
    }

}
