package io.github.codeutilities.sys.modules.actions.json;

import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.modules.translations.Translation;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionJson extends JSONObject {
    public ActionJson(JSONObject json, ModuleJson module, HashMap<String, Object> variables) {
        super(json.toString());

        this.module = module;
        this.variables = variables;
    }

    private final HashMap<String, Object> variables;
    private final ModuleJson module;

    @Override
    public String getString(String key) {
        String value = (String) this.get(key);

        if (value.contains("${")) {
            Pattern pattern = Pattern.compile("\\$\\{([a-z]|\\.)+}");
            Matcher matcher = pattern.matcher(value);

            List<String> matches = new ArrayList<>();

            int i = 0;
            while (matcher.find()) {
                String match = matcher.group(i);
                matches.add(match);
            }
            for (String match : matches) {
                String result = "null";
                String moduleId = module.getId();

                String varname = match.replaceAll("^\\$\\{|}$", "");
                String noprefix = varname.replaceFirst("^[a-z]+.", "");

                if (varname.startsWith("config.")) {
                    result = Config.getString(noprefix);
                } else if (varname.startsWith("translation.")) {
                    result = Translation.get("module."+moduleId+"."+noprefix);
                } else if (varname.startsWith("meta.")) {
                    result = Translation.get("module."+moduleId+".meta."+noprefix);
                } else if (varname.startsWith("event.")) {
                    result = (String) variables.get(varname);
                } else if (varname.startsWith("custom.")) {
                    result = (String) variables.get(varname);
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

    public String getId() {
        return this.getString("action");
    }

}
