package io.github.codeutilities.modules.tasks;

import io.github.codeutilities.modules.Module;
import io.github.codeutilities.modules.actions.Action;
import io.github.codeutilities.modules.actions.json.ActionJson;
import io.github.codeutilities.modules.actions.json.ModuleJson;
import io.github.codeutilities.modules.triggers.Trigger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class Task {

    private static HashMap<String, JSONArray> TASK_ACTIONS = new HashMap<>();

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
        // --- load variables
        HashMap<String, Object> VARIABLES = new HashMap<>();

        // load event vars
        String[] eventVarNames = trigger.getEventVars();
        int i = 0;
        for (Object var : eventVars) {
            VARIABLES.put("event."+eventVarNames[i], var);
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

    public static void putActions(String taskName, JSONArray actions) {
        TASK_ACTIONS.put(taskName, actions);
    }

}
