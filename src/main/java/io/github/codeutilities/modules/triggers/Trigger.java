package io.github.codeutilities.modules.triggers;

import io.github.codeutilities.modules.tasks.Task;
import io.github.codeutilities.modules.triggers.impl.MessageReceivedTrigger;
import io.github.codeutilities.modules.triggers.impl.StateChangeTrigger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Trigger {

    private static Trigger[] TRIGGERS = new Trigger[]{ new StateChangeTrigger(), new MessageReceivedTrigger()};
    // triggerId, trigger
    private static HashMap<String, Trigger> ID_TRIGGERS = new HashMap<>();

    // triggerId, tasks
    private static HashMap<String, String[]> TRIGGER_TASKS = new HashMap<>();

    public static void cacheTriggers() {
        for (Trigger trigger : TRIGGERS) {
            ID_TRIGGERS.put(trigger.getId(), trigger);
        }
    }

    public String getId() {
        return null;
    }

    public static Trigger getTrigger(String id) {
        return ID_TRIGGERS.get(id);
    }

    public static void putTask(Trigger trigger, String taskName, String moduleId) {
        taskName = moduleId+"."+taskName;

        TRIGGER_TASKS.putIfAbsent(trigger.getId(), new String[]{});
        List<String> currentTasks = new ArrayList<>(Arrays.asList(TRIGGER_TASKS.get(trigger.getId())));
        currentTasks.add(taskName);
        String[] newTasks = currentTasks.toArray(new String[0]);

        TRIGGER_TASKS.put(trigger.getId(), newTasks);
    }

    public static String[] getTasks(Trigger trigger) {
        return TRIGGER_TASKS.get(trigger.getId());
    }

    public static void execute(Trigger trigger) {
        String[] tasks = getTasks(trigger);
        Task.execute(tasks);
    }

}
