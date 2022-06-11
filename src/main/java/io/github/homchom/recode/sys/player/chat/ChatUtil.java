package io.github.homchom.recode.sys.player.chat;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.player.chat.color.MinecraftColors;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.*;
import net.minecraft.sounds.*;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class ChatUtil {

    public static void playSound(SoundEvent sound) {
        playSound(sound, 1F);
    }

    public static void playSound(SoundEvent sound, float pitch) {
        playSound(sound, 2F, pitch);
    }

    public static void playSound(SoundEvent sound, float pitch, float volume) {
        if (sound != null) {
            LegacyRecode.MC.player.playSound(sound, volume, pitch);
        }
    }

    public static void chat(String message) {
        LegacyRecode.MC.player.chat(message);
    }

    public static void executeCommand(String command) {
        chat("/" + command.replaceFirst("^/", ""));
    }

    public static void executeCommandSilently(String command, int messageAmount) {
        executeCommand(command);
        MessageGrabber.hide(messageAmount);
    }

    public static void executeCommandSilently(String command) {
        executeCommandSilently(command, 1);
    }

    public static void sendMessage(String text) {
        sendMessage(new TextComponent(text), null);
    }

    public static void sendMessage(String text, ChatType prefixType) {
        sendMessage(new TextComponent(text), prefixType);
    }

    public static void sendTranslateMessage(String identifier, ChatType prefixType) {
        sendMessage(new TranslatableComponent(identifier), prefixType);
    }

    public static void sendMessage(TranslatableComponent component, ChatType prefixType) {
        sendMessage((BaseComponent) component, prefixType);
    }

    public static void sendMessage(MutableComponent text, @Nullable ChatType chatType) {
        LocalPlayer player = LegacyRecode.MC.player;
        if (player == null) {
            return;
        }

        if (chatType == null) {
            player.displayClientMessage(text, false);
        } else {
            MinecraftColors minecraftColors = MinecraftColors.fromCode(chatType.getTrailing());
            if (minecraftColors == null) {
                minecraftColors = MinecraftColors.RED;
            }

            ChatUtil.setColor(text, minecraftColors.getColor());
            player.displayClientMessage(new TextComponent(chatType.getString() + " ").append(text), false);
            if (chatType == ChatType.FAIL) {
                if (Config.getBoolean("errorSound")) {
                    player.playNotifySound(SoundEvents.NOTE_BLOCK_DIDGERIDOO, SoundSource.PLAYERS, 2, 0);
                }
            }
        }
    }

    /**
     *     A hacky way of verifying that a message is sent by Hypercube.
     *     im too lazy to reflect or use a mixin. don't ask
     *
     *     * Doesn't work
     */
    public static boolean verifyMessage(Component component) {
        List<Component> siblings = component.getSiblings();
        if (!DFInfo.isOnDF()) return false;
        if (siblings.size() == 0) return false;
        String str = siblings.get(0).getStyle().toString();

        return !(str.contains("bold=null") ||
                str.contains("italic=null") ||
                str.contains("underlined=null") ||
                str.contains("strikethrough=null") ||
                str.contains("obfuscated=null"));
    }

    public static MutableComponent setColor(MutableComponent component, Color color) {
        Style colorStyle = component.getStyle().withColor(TextColor.fromRgb(color.getRGB()));
        component.setStyle(colorStyle);
        return component;
    }

}
