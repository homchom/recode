package io.github.homchom.recode.sys.sidedchat;

import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

import java.util.List;

public class ChatPattern {
    private final List<ChatComponent> chatComponents;

    public ChatPattern(ChatComponent... chatComponents) {
        this.chatComponents = Lists.newArrayList(chatComponents);
    }

    public ChatPattern(Component text) {
        this.chatComponents = Lists.newArrayList();
        populateChatComponents(text);
    }

    public boolean contains(ChatPattern subChatPattern) {
        int correctInARow = 0; // also used to determine which component in the sub pattern to check
        int currentPos = 0;
        for (ChatComponent chatComponent : chatComponents) {
            ChatComponent subChatComponent = subChatPattern.chatComponents.get(correctInARow);
            if (chatComponent.equals(subChatComponent) && chatComponent.posEquals(currentPos)) {
                correctInARow++; // will check next sub component next iteration
                if (correctInARow == subChatPattern.chatComponents.size()) return true;
            }
            else correctInARow = 0;
            currentPos++;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (ChatComponent chatComponent : chatComponents) {
            stringBuilder.append(chatComponent.toString()).append("\n");
        }
        return stringBuilder.toString();
    }

    private void populateChatComponents(Component text) {
        if (text.getSiblings().isEmpty()) { // only display the bottom most nodes
            chatComponents.add(new ChatComponent(text.getString(),text.getStyle().getColor()));
        }
        else for (Component childText : text.getSiblings()) populateChatComponents(childText);
    }

    public static class ChatComponent {
        private final String string;
        private final TextColor color;
        private int pos = -1;

        public ChatComponent(String string, TextColor color) {
            this.string = string;
            this.color = color;
        }

        public ChatComponent(String string, TextColor color, int pos) {
            this.string = string;
            this.color = color;
            this.pos = pos;
        }

        public boolean equals(ChatComponent chatComponent) {
            boolean stringEquals = false;
            // if either strings are null then its assumed 'match any'
            if (this.string == null || chatComponent.string == null) stringEquals = true;
            else if (this.string.equals(chatComponent.string)) stringEquals = true;

            boolean colorEquals = false;
            // same for colour
            if (this.color == null || chatComponent.color == null) colorEquals = true;
            else if (this.color.equals(chatComponent.color)) colorEquals = true;

            return stringEquals && colorEquals;
        }

        private boolean posEquals(int pos) {
            return this.pos == -1 || this.pos == pos;
        }

        @Override
        public String toString() {
            if (color != null)
                return "ChatComponent{" +
                        "string='" + string + '\'' +
                        ", color=" + color.serialize() +
                        '}';
            else
                return "ChatComponent{" +
                        "string='" + string + '\'' +
                        ", color=" + "null" +
                        '}';

        }
    }
}

