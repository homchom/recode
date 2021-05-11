package io.github.codeutilities.events;

import io.github.codeutilities.commands.sys.IManager;
import io.github.codeutilities.events.register.ReceiveChatMessageEvent;

import java.util.ArrayList;
import java.util.List;

public class EventHandler implements IManager<Object> {
    private final List<Object> registeredEvents = new ArrayList<>();

    @Override
    public void initialize() {
        register(
                new ReceiveChatMessageEvent()
        );
    }

    @Override
    public void register(Object object) {
        registeredEvents.add(object);
    }

    public void register(Object... objects) {
        for (Object object : objects) register(object);
    }

    @Override
    public List<Object> getRegistered() {
        return registeredEvents;
    }
}
