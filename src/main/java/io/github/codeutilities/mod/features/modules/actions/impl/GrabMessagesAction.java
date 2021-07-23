package io.github.codeutilities.mod.features.modules.actions.impl;

import io.github.codeutilities.mod.features.modules.actions.Action;
import io.github.codeutilities.mod.features.modules.actions.json.ActionJson;
import io.github.codeutilities.mod.features.modules.tasks.Task.TaskExecutorThread;
import io.github.codeutilities.sys.player.chat.MessageGrabber;
import io.github.codeutilities.sys.util.TextUtil;
import net.minecraft.text.Text;

import java.util.concurrent.atomic.AtomicInteger;

public class GrabMessagesAction extends Action {

    @Override
    public String getId() {
        return "grabMessages";
    }

    @Override
    public void execute(ActionJson params) {
        int amount = params.getInt("amount");
        String variable = params.getString("variable");
        TaskExecutorThread thread = (TaskExecutorThread) params.get("_thread");

        System.out.println("grabMessages "+amount);

        AtomicInteger i = new AtomicInteger();
        MessageGrabber.grab(amount, msgs -> {
            for (Text txt : msgs) {
                i.getAndIncrement();
                thread.putVariable("custom."+variable+"."+i,
                        TextUtil.textComponentToColorCodes(txt));
            }
        });

        while (i.get() != amount) {
            System.out.println(i.get() + " "+amount);
            try { Thread.sleep(100);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }

    }

}
