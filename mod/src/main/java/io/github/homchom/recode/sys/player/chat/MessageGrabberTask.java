package io.github.homchom.recode.sys.player.chat;

import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessageType;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

public class MessageGrabberTask {

    public int messages;
    public Consumer<List<Component>> consumer;
    public boolean silent;
    public LegacyMessageType filter;

    public MessageGrabberTask(int messages, Consumer<List<Component>> consumer, boolean silent, LegacyMessageType filter) {
        this.messages = messages;
        this.consumer = consumer;
        this.silent = silent;
        this.filter = filter;
    }

}
