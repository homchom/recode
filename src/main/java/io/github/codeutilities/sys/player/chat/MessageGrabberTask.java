package io.github.codeutilities.sys.player.chat;

import io.github.codeutilities.mod.features.social.chat.message.MessageCheck;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.text.Text;

public class MessageGrabberTask {

    public int messages;
    public Consumer<List<Text>> consumer;
    public boolean silent;
    public MessageCheck filter;

    public MessageGrabberTask(int messages, Consumer<List<Text>> consumer, boolean silent, MessageCheck filter) {
        this.messages = messages;
        this.consumer = consumer;
        this.silent = silent;
        this.filter = filter;
    }

}
