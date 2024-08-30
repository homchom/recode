package io.github.homchom.recode.sys.hypercube.codeaction;

import com.google.gson.JsonObject;

public class CodeBlock {
    private final String name;
    private final String identifier;
    private final DisplayItem item;

    CodeBlock(JsonObject jsonObject){
        this.name = jsonObject.get("name").getAsString();
        this.identifier = jsonObject.get("identifier").getAsString();
        this.item = new DisplayItem(jsonObject.getAsJsonObject("item"));
    }

    public String getName() {
        return name;
    }

    public DisplayItem getItem() {
        return item;
    }

    public String getIdentifier() {
        return identifier;
    }
}