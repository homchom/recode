package io.github.codeutilities.modules.tasks;

import io.github.codeutilities.modules.Module;
import io.github.codeutilities.modules.actions.Action;
import io.github.codeutilities.modules.actions.json.ActionJson;
import io.github.codeutilities.modules.actions.json.ModuleJson;
import io.github.codeutilities.modules.triggers.Trigger;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Task {

    private static final HashMap<String, JSONArray> TASK_ACTIONS = new HashMap<>();

    public static void execute(String[] tasks, Trigger trigger, Object[] eventVars) {
        for (String task : tasks) {
            execute(task, trigger, eventVars);
        }
    }

    public static void execute(String task, Trigger trigger, Object[] eventVars) {
        JSONArray actions = TASK_ACTIONS.get(task);

        String moduleId = task.replaceAll("\\..*$", "");
        ModuleJson module = Module.getModule(moduleId);

        execute(actions, module, trigger, eventVars);
    }

    public static void execute(JSONArray actions, ModuleJson module, Trigger trigger, Object[] eventVars) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        TaskExecutorThread thread = new TaskExecutorThread(actions, module, trigger, eventVars);
        service.submit(thread);
    }

    public static class TaskExecutorThread extends Thread {
        public TaskExecutorThread(JSONArray actions, ModuleJson module, Trigger trigger, Object[] eventVars) {
            this.actions = actions;
            this.module = module;
            this.trigger = trigger;
            this.eventVars = eventVars;
        }

        private final JSONArray actions;
        private final ModuleJson module;
        private final Trigger trigger;
        private final Object[] eventVars;

        @Override
        public void run() {
            // --- load variables
            HashMap<String, Object> VARIABLES = new HashMap<>();

            // load event vars
            String[] eventVarNames = trigger.getEventVars();
            int i = 0;
            for (Object var : eventVars) {
                VARIABLES.put("event." + eventVarNames[i], var);
                i++;
            }

            // --- executor
            for (i = 0; i < actions.length(); i++) {
                ActionJson actionObj = new ActionJson(actions.getJSONObject(i));
                String actionId = actionObj.getId();

                actionObj.setVars(VARIABLES);
                actionObj.setModule(module);

                Action action = Action.getAction(actionId);
                action.execute(actionObj);
            }
        }
    }

    public static void putActions(String taskName, JSONArray actions) {
        TASK_ACTIONS.put(taskName, actions);
    }

}
