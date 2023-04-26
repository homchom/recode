package io.github.homchom.recode.sys.player.chat;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.player.chat.color.MinecraftColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

    public static void command(String message) {
        Minecraft.getInstance().player.connection.sendUnsignedCommand(message);
    }

    public static void executeCommand(String command) {
        command(command.replaceFirst("^/", ""));
    }

    public static void executeCommandSilently(String command, int messageAmount) {
        executeCommand(command);
        MessageGrabber.hide(messageAmount);
    }

    public static void executeCommandSilently(String command) {
        executeCommandSilently(command, 1);
    }

    public static void sendMessage(String text) {
        sendMessage(Component.literal(text), null);
    }

    public static void sendMessage(String text, ChatType prefixType) {
        sendMessage(Component.literal(text), prefixType);
    }

    public static void sendTranslateMessage(String identifier, ChatType prefixType) {
        sendMessage(Component.translatable(identifier), prefixType);
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
            player.displayClientMessage(Component.literal(chatType.getString() + " ").append(text), false);
            if (chatType == ChatType.FAIL) {
                if (Config.getBoolean("errorSound")) {
                    player.playNotifySound(SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), SoundSource.PLAYERS, 2, 0);
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
