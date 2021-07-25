package io.github.codeutilities.mod.features.social.tab;

import com.google.common.base.Strings;
import io.github.codeutilities.sys.util.StringUtil;

public class Message {
    private final String type;
    private final Object content;
    private final String id;

    public Message(String type, Object content, String id) {
        this.type = type;
        this.content = content;
        this.id = id == null ? StringUtil.generateKey(6) : id;
    }

    public Message(String type, Object content) {
        this.type = type;
        this.content = content;
        this.id = StringUtil.generateKey(6);
    }

    public Message(String type) {
        this.type = type;
        this.content = null;
        this.id = StringUtil.generateKey(6);
    }

    public String build() {
        boolean isString = this.content instanceof String && !((String) this.content).startsWith("{") && !((String) this.content).endsWith("}") && !((String) this.content).startsWith("[") && !((String) this.content).endsWith("]");
        return "{\"type\":\"" + this.type + "\"" + (Strings.isNullOrEmpty((String) this.content) ? "" : ",\"content\":" + (isString ? "\"" : "") + this.content + (isString ? "\"" : "")) + ",\"id\":\"" + this.id + "\"" + "}";
    }

    public String getType() {
        return type;
    }

    public Object getContent() {
        return content;
    }

    public String getId() {
        return id;
    }
}
