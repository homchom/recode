package io.github.codeutilities.util.actiondump;

import com.google.gson.JsonObject;

public class TagOption {
    private final String name;
    private final DisplayItem icon;

    TagOption(JsonObject jsonObject){
        this.name = jsonObject.get("name").getAsString();
        this.icon = new DisplayItem(jsonObject.getAsJsonObject("icon"));
    }

    public String getName() {
        return name;
    }

    public DisplayItem getIcon() {
        return icon;
    }
}
