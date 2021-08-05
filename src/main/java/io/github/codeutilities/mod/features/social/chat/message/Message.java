package io.github.codeutilities.mod.features.social.chat.message;

import net.minecraft.text.Text;

public class Message {

    private final Text text;
    private final MessageType type;

    public Message(Text text) {
        this.text = text;
        this.type = MessageCheck.run(this);
    }

    public Text text() {
        return text;
    }

    public MessageType type() {
        return type;
    }
}
