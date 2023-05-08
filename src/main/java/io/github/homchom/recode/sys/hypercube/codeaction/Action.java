package io.github.homchom.recode.sys.hypercube.codeaction;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.homchom.recode.sys.util.StringUtil;

public class Action {
    private final String name;
    private final CodeBlock codeBlock;
    private final Tag[] tags;
    private final String[] aliases;
    private final DisplayItem icon;

    Action(JsonObject jsonObject){
        this.name = jsonObject.get("name").getAsString();
        this.codeBlock = ActionDump.getCodeBlock(jsonObject.get("codeblockName").getAsString()).get(0);
        this.tags = new Tag[jsonObject.getAsJsonArray("tags").size()];
        int i = 0;
        for(JsonElement tag : jsonObject.getAsJsonArray("tags")){
            this.tags[i] = new Tag(tag.getAsJsonObject());
            i++;
        }
        this.aliases = StringUtil.toStringArray(jsonObject.get("aliases").getAsJsonArray());
        this.icon = new DisplayItem(jsonObject.getAsJsonObject("icon"));
    }

    public String getName() {
        return name;
    }

    public CodeBlock getCodeBlock() {
        return codeBlock;
    }

    public DisplayItem getIcon() {
        return icon;
    }

    public String[] getAliases() {
        return aliases;
    }

    public Tag[] getTags() {
        return tags;
    }
}