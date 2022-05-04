package io.github.homchom.recode.sys.hypercube.codeaction;

import com.google.gson.*;

public class Tag {
    private final String name;
    private final TagOption[] options;

    Tag(JsonObject jsonObject){
        this.name = jsonObject.get("name").getAsString();
        this.options = new TagOption[jsonObject.getAsJsonArray("options").size()];
        int i = 0;
        for(JsonElement option : jsonObject.getAsJsonArray("options")){
            this.options[i] = new TagOption(option.getAsJsonObject());
            i++;
        }
    }

    public String getName() {
        return name;
    }

    public TagOption[] getOptions() {
        return options;
    }
}
