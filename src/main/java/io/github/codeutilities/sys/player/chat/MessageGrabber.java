package io.github.codeutilities.sys.player.chat;

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

    public static void grab(int messages, Consumer<List<Text>> consumer) {
        messagesToGrab = messages;
        messageConsumer = consumer;
        silent = false;
    }

    public static void grabSilently(int messages, Consumer<List<Text>> consumer) {
        messagesToGrab = messages;
        messageConsumer = consumer;
        silent = true;
    }

    public static void hideSilently(int messages) {
        if (messages > 0) grabSilently(messages, ignored -> {});
    }

    public static void supply(Text message) {
        currentMessages.add(message);

        if (currentMessages.size() >= messagesToGrab) {
            messageConsumer.accept(currentMessages);
            currentMessages.clear();
            messagesToGrab = 0;
            messageConsumer = null;
        }
    }

    public static boolean isActive() {
        return messageConsumer != null;
    }

    public static boolean isSilent() {
        return silent;
    }
}
