package io.github.codeutilities.modules.tasks;

import io.github.codeutilities.modules.actions.Action;
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
        execute(actions);
    }

    public static void execute(JSONArray actions) {
        for (int i = 0; i < actions.length(); i++) {
            JSONObject actionObj = actions.getJSONObject(i);
            String actionId = actionObj.getString("action");

            Action action = Action.getAction(actionId);
            action.execute(actionObj);
        }
    }

    public static void putActions(String taskName, JSONArray actions) {
        TASK_ACTIONS.put(taskName, actions);
    }

}
