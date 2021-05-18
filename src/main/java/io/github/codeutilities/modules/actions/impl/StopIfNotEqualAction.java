package io.github.codeutilities.modules.actions.impl;

import io.github.codeutilities.modules.actions.Action;
import io.github.codeutilities.modules.actions.json.ActionJson;
import io.github.codeutilities.modules.tasks.Task.TaskExecutorThread;

public class StopIfNotEqualAction extends Action {

    @Override
    public String getId() {
        return "stopIfNotEqual";
    }

    @Override
    public void execute(ActionJson params) {
        String a = params.getString("a");
        String b = params.getString("b");

        if (!a.equals(b)) {
            TaskExecutorThread.status = -1;
        }
    }

}
