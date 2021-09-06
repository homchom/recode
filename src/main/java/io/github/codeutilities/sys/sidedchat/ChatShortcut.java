package io.github.codeutilities.sys.sidedchat;

import net.minecraft.client.options.KeyBinding;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;

public enum ChatShortcut {
    SUPPORT_CHAT("support_chat", new Color(0,   148, 193, 100), "/sb "),
    MOD_CHAT    ("mod_chat",     new Color(0,   178, 18,  100), "/mb "),
    ADMIN_CHAT  ("admin_chat",   new Color(178, 0,   0,   100), "/ab ");

    private static final Map<KeyBinding, ChatShortcut> CHAT_SHORTCUTS = new HashMap<>();
    @Nullable
    private static ChatShortcut currentChatShortcut = null;

    @Nullable
    public static ChatShortcut getCurrentChatShortcut() {
        return currentChatShortcut;
    }

    public static void setCurrentChatShortcut(@Nullable ChatShortcut chatShortcut) {
        ChatShortcut.currentChatShortcut = chatShortcut;
    }

    String translationKey;
    Color color;
    String prefix;

    public String getTranslationKey() {
        return "key.codeutilities." + translationKey;
    }

    public Color getColor() {
        return color;
    }

    public String getPrefix() {
        return prefix;
    }

    ChatShortcut(String translationKey, Color color, String prefix) {
        this.translationKey = translationKey;
        this.color = color;
        this.prefix = prefix;
    }

    /**
     * Links a KeyBinding & a Shortcut together
     */
    public static void addKeyBinding(KeyBinding keyBinding, ChatShortcut chatShortcut) {
        CHAT_SHORTCUTS.put(keyBinding, chatShortcut);
    }

    /**
     * Returns a collection of all the keybindings
     * This is processed inside the KeyBinds class
     */
    public static Set<KeyBinding> keyBindings() {
        return CHAT_SHORTCUTS.keySet();
    }

    /**
     * Gets a chat shortcut from a keybinding
     * Intended for when a shortcut is pressed to lookup info about it
     * May fail if it can't find that keybinding
     */
    public static ChatShortcut getFromKey(KeyBinding keyBinding) {
        return CHAT_SHORTCUTS.get(keyBinding);
    }
}
