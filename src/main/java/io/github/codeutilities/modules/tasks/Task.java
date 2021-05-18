package io.github.codeutilities.modules.tasks;

import io.github.codeutilities.modules.Module;
import io.github.codeutilities.modules.actions.Action;
import io.github.codeutilities.modules.actions.json.ActionJson;
import io.github.codeutilities.modules.actions.json.ModuleJson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class Task {

    private static HashMap<String, JSONArray> TASK_ACTIONS = new HashMap<>();

    public static void execute(String[] tasks) {
        for (String task : tasks) {
            execute(task);
        }
    }

    public static void execute(String task) {
        JSONArray actions = TASK_ACTIONS.get(task);

        String moduleId = task.replaceAll("\\..*$", "");
        ModuleJson module = Module.getModule(moduleId);

        execute(actions, module);
    }

    public static void execute(JSONArray actions, ModuleJson module) {
        // load variables
        HashMap<String, Object> VARIABLES = new HashMap<>();


        // executor
        for (int i = 0; i < actions.length(); i++) {
            ActionJson actionObj = (ActionJson) actions.getJSONObject(i);
            actionObj.setVars(VARIABLES);
            String actionId = actionObj.getString("action");

            Action action = Action.getAction(actionId);
            action.execute(actionObj);
        }
    }

    public static void putActions(String taskName, JSONArray actions) {
        TASK_ACTIONS.put(taskName, actions);
    }

}
