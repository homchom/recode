package io.github.codeutilities.sys.player.chat;

import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageCheck;
import io.github.codeutilities.mod.features.social.chat.message.MessageType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A utility class to grab the next X chat messages.
 */
public class MessageGrabber {

    private static final List<Text> currentMessages = new ArrayList<>();
    private static Consumer<List<Text>> messageConsumer;
    private static int messagesToGrab = 0;
    private static boolean silent = false;
    private static MessageType filter = null;
    private static final List<MessageGrabberTask> tasks = new ArrayList<>();

    public static void grab(int messages, Consumer<List<Text>> consumer, MessageType filter) {
        if (isActive()) {
            tasks.add(new MessageGrabberTask(messages,consumer,false, filter));
            return;
        }
        messagesToGrab = messages;
        messageConsumer = consumer;
        silent = false;
        MessageGrabber.filter = filter;
    }

    public static void grab(int messages, Consumer<List<Text>> consumer) {
        grab(messages,consumer,null);
    }

    public static void grabSilently(int messages, Consumer<List<Text>> consumer, MessageType filter) {
        if (isActive()) {
            tasks.add(new MessageGrabberTask(messages,consumer,true, filter));
            return;
        }
        messagesToGrab = messages;
        messageConsumer = consumer;
        silent = true;
        MessageGrabber.filter = filter;
    }

    public static void grabSilently(int messages, Consumer<List<Text>> consumer) {
        grabSilently(messages,consumer, null);
    }

    public static void hideNext() {
        hide(1);
    }

    public static void hide(int messages) {
        if (messages > 0) grabSilently(messages, ignored -> {}, null);
    }
    public static void hide(int messages, MessageType filter) {
        if (messages > 0) grabSilently(messages, ignored -> {}, filter);
    }

    public static void supply(Message msg) {
        if (filter != null && !msg.typeIs(filter)) return;

        Text message = msg.getText();
        currentMessages.add(message);

        if (silent) msg.cancel();

        if (currentMessages.size() >= messagesToGrab) {
            messageConsumer.accept(currentMessages);
            currentMessages.clear();
            messagesToGrab = 0;
            messageConsumer = null;


            if (tasks.size() > 0) {
                MessageGrabberTask task = tasks.remove(0);
                messagesToGrab = task.messages;
                messageConsumer = task.consumer;
                silent = task.silent;
            }
        }
    }

    public static void reset() {
        tasks.clear();
        messageConsumer = null;
        messagesToGrab = 0;
        silent = false;
        filter = null;
    }

    public static boolean isActive() {
        return messageConsumer != null;
    }

    public static boolean isSilent() {
        return silent;
    }
}
