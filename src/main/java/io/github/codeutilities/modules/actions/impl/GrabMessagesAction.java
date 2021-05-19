package io.github.codeutilities.modules.actions.impl;

import io.github.codeutilities.modules.actions.Action;
import io.github.codeutilities.modules.actions.json.ActionJson;
import io.github.codeutilities.util.chat.MessageGrabber;

public class GrabMessagesAction extends Action {

    @Override
    public String getId() {
        return "grabMessages";
    }

    @Override
    public void execute(ActionJson params) {
        MessageGrabber.grab(3, e -> System.out.println("!!!!!! "+e));
    }

}
