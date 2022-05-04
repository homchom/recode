package io.github.homchom.recode.mod.features.social.tab;

import com.google.gson.*;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.sys.util.StringUtil;

public class WebMessage {
    private final String type;
    private final JsonElement content;
    private final String id;

    /**
     * content can be either {@link String}, {@link Number}, {@link Boolean} or some type of {@link JsonElement}
     * @param type
     * @param content
     * @param id
     */
    public WebMessage(String type, Object content, String id) {
        this.type = type;
        this.content = parseContent(content);
        this.id = id == null ? StringUtil.generateKey(6) : id;
    }

    /**
     * content can be either {@link String}, {@link Number}, {@link Boolean} or some type of {@link JsonElement}
     * @param type
     * @param content
     */
    public WebMessage(String type, Object content) {
        this.type = type;
        this.content = parseContent(content);
        this.id = StringUtil.generateKey(6);
    }

    public WebMessage(String type) {
        this.type = type;
        this.content = parseContent("");
        this.id = StringUtil.generateKey(6);
    }

    public String build() {
        return "{\"type\":\"" + this.type + "\",\"content\":" + this.content.toString() + ",\"id\":\"" + this.id + "\"" + "}";
    }

    private JsonElement parseContent(Object content) {
        if(content instanceof String) {
            //if((((String) content).startsWith("{") && ((String) content).endsWith("}")) || (((String) content).startsWith("[") && ((String) content).endsWith("]"))) {
                //return Recode.JSON_PARSER.parse((String) content);
            //} else {
                return (JsonElement) new JsonPrimitive((String) content);
            //}
        } else if(content instanceof Number) {
            return (JsonElement) new JsonPrimitive((Number) content);
        } else if(content instanceof Boolean) {
            return (JsonElement) new JsonPrimitive((Boolean) content);
        } else if(content instanceof JsonElement) {
            return Recode.JSON_PARSER.parse(((JsonElement) content).toString());
        }
        return null;
    }

    public String getType() {
        return type;
    }

    /**
     * Content is a JsonElement which means it has many different types
     * u can use like {@link JsonElement#getAsInt()} or {@link JsonElement#getAsJsonObject()}
     * but do not use the wrong one for the wrong type (thats bad)
     * 👍👍👍👍👍👍👍👍👍👍
     */
    public JsonElement getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.build();
    }
}
