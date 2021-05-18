package io.github.codeutilities.modules.actions;

import io.github.codeutilities.modules.actions.impl.*;
import io.github.codeutilities.modules.actions.json.ActionJson;

import java.util.HashMap;

public class Action {

    private static final Action[] ACTIONS = new Action[]{
            new MessageAction(),
            new CancelNextMessagesAction(),
            new SendMessageAction(),
            new StopIfEqualAction(),
            new ContinueIfEqual(),
            new WaitAction()
    };
    // actionId, action
    private static final HashMap<String, Action> ACTION_IDS = new HashMap<>();

    public static void cacheActions() {
        for (Action action : ACTIONS) {
            ACTION_IDS.put(action.getId(), action);
        }
    }

    public String getId() {
        return null;
    }

    public void execute(ActionJson params) {
    }

    public static Action getAction(String id) {
        return ACTION_IDS.get(id);
    }

}
