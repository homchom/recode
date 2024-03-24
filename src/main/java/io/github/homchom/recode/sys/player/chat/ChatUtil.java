package io.github.homchom.recode.sys.player.chat;

import io.github.homchom.recode.mod.config.LegacyConfig;
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

public class ChatUtil {
    public static void playSound(SoundEvent sound) {
        playSound(sound, 1F);
    }

    public static void playSound(SoundEvent sound, float pitch) {
        playSound(sound, 2F, pitch);
    }

    public static void playSound(SoundEvent sound, float pitch, float volume) {
        if (sound != null) {
            Minecraft.getInstance().player.playSound(sound, volume, pitch);
        }
    }

    public static void command(String message) {
        Minecraft.getInstance().player.connection.sendUnsignedCommand(message);
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
        LocalPlayer player = Minecraft.getInstance().player;
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
                if (LegacyConfig.getBoolean("errorSound")) {
                    player.playNotifySound(SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), SoundSource.PLAYERS, 2, 0);
                }
            }
        }
    }

    public static MutableComponent setColor(MutableComponent component, Color color) {
        Style colorStyle = component.getStyle().withColor(TextColor.fromRgb(color.getRGB()));
        component.setStyle(colorStyle);
        return component;
    }

}
