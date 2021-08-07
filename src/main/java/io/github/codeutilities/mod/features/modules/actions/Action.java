package io.github.codeutilities.mod.features.modules.actions;

import io.github.codeutilities.mod.features.modules.actions.impl.*;
import io.github.codeutilities.mod.features.modules.actions.json.ActionJson;

import java.util.HashMap;

public abstract class Action {

    private static final Action[] ACTIONS = new Action[]{
            new MessageAction(),
            new CancelNextMessagesAction(),
            new SendMessageAction(),
            new StopIfEqualAction(),
            new ContinueIfEqual(),
            new WaitAction(),
            new GrabMessagesAction()
    };
    // actionId, action
    private static final HashMap<String, Action> ACTION_IDS = new HashMap<>();

    public static void cacheActions() {
        for (Action action : ACTIONS) {
            ACTION_IDS.put(action.getId(), action);
        }
    }

    public abstract String getId();

    public abstract void execute(ActionJson params);

    public static Action getAction(String id) {
        return ACTION_IDS.get(id);
    }

}
