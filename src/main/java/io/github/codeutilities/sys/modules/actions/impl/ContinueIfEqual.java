package io.github.codeutilities.sys.modules.actions.impl;

import io.github.codeutilities.sys.modules.actions.Action;
import io.github.codeutilities.sys.modules.actions.json.ActionJson;
import io.github.codeutilities.sys.modules.tasks.Task.TaskExecutorThread;

public class ContinueIfEqual extends Action {

    @Override
    public String getId() {
        return "continueIfEqual";
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
