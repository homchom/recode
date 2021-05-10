package io.github.codeutilities.events.register;

import io.github.codeutilities.commands.sys.IManager;
import io.github.codeutilities.util.file.ILoader;

import java.util.ArrayList;
import java.util.List;

public class EventRegisterer implements IManager<ILoader> {
    private static EventRegisterer instance;
    private final List<ILoader> registeredEvents = new ArrayList<>();

    public EventRegisterer() {
        instance = this;
    }

    public static EventRegisterer getInstance() {
        return instance;
    }

    @Override
    public void initialize() {
        for (ILoader loader : registeredEvents) loader.load();
    }

    @Override
    public void register(ILoader object) {
        registeredEvents.add(object);
    }

    @Override
    public List<ILoader> getRegistered() {
        return registeredEvents;
    }
}
