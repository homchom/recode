package io.github.homchom.recode.mod.events;

import io.github.homchom.recode.mod.commands.IManager;
import io.github.homchom.recode.mod.events.impl.*;

import java.util.*;

public class EventHandler implements IManager<Object> {
    private final List<Object> registeredEvents = new ArrayList<>();

    @Override
    public void initialize() {
        register(
                new ReceiveChatMessageEvent(),
                new ChangeStateEvent(),
                new TickEvent(),
                new AfterScreenInitEvent()
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
