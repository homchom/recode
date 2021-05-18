package io.github.codeutilities.modules.actions.json;

import org.json.JSONObject;

import java.util.HashMap;

public class ActionJson extends JSONObject {

    private HashMap<String, Object> VARIABLES = new HashMap<>();

    @Override
    public String getString(String key) {
        return (String) this.get(key);
    }

    public void setVars(HashMap<String, Object> variableMap) {
        this.VARIABLES = variableMap;
    }

}
