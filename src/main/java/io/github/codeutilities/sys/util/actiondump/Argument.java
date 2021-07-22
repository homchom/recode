package io.github.codeutilities.sys.util.actiondump;

import com.google.gson.JsonObject;
import io.github.codeutilities.sys.util.misc.StringUtil;

import java.util.Arrays;
import java.util.HashSet;

public class Argument {
    private final String type;
    private final boolean plural;
    private final boolean optional;
    private final String[] description;
    private final HashSet<String[]> notes;
    private final String text;

    Argument(JsonObject jsonObject){
        if(jsonObject.has("text")){
            this.type = null;
            this.text = jsonObject.get("text").getAsString();
            this.plural = false;
            this.optional = false;
            this.description = null;
            this.notes = null;
        }else{
            this.type = jsonObject.get("type").getAsString();
            this.plural = jsonObject.get("plural").getAsBoolean();
            this.optional = jsonObject.get("optional").getAsBoolean();
            this.description = StringUtil.toStringArray(jsonObject.get("description").getAsJsonArray());
            this.notes = StringUtil.toStringListHashSet(jsonObject.get("notes").getAsJsonArray());
            this.text = null;
        }
    }

    public String[] getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public HashSet<String[]> getNotes() {
        return notes;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isPlural() {
        return plural;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Argument{" +
                "type='" + type + '\'' +
                ", plural=" + plural +
                ", optional=" + optional +
                ", description=" + Arrays.toString(description) +
                ", notes=" + notes +
                ", text='" + text + '\'' +
                '}';
    }
}

