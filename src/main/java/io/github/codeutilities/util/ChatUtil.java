package io.github.codeutilities.util;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class ChatUtil {

    public static void sendMessage(String text) {
        sendMessage(new LiteralText(text), null);
    }

    public static void sendMessage(String text, ChatType prefixType) {
        sendMessage(new LiteralText(text), prefixType);
    }

    public static void sendTranslateMessage(String identifier, ChatType prefixType) {
        sendMessage(new TranslatableText(identifier), prefixType);
    }

    public static void sendMessage(TranslatableText component, ChatType prefixType) {
        sendMessage((BaseText) component, prefixType);
    }

    public static void sendMessage(MutableText text, @Nullable ChatType chatType) {
        ClientPlayerEntity player = CodeUtilities.MC.player;
        if (player == null) {
            return;
        }

        if (chatType == null) {
            player.sendMessage(text, false);
        } else {
            MinecraftColors minecraftColors = MinecraftColors.fromCode(chatType.getTrailing());
            if (minecraftColors == null) {
                minecraftColors = MinecraftColors.RED;
            }

            ChatUtil.setColor(text, minecraftColors.getColor());
            player.sendMessage(new LiteralText(chatType.getString() + " ").append(text), false);
            if (chatType == ChatType.FAIL) {
                if (ModConfig.getConfig().errorSound) {
                    player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 2, 0);
                }
            }
        }
    }

    // A hacky way of verifying that a message is sent by Hypercube.
    public static boolean verifyMessage(Text component) {
        List<Text> siblings = component.getSiblings();
        if (!DFInfo.isOnDF()) return false;
        if (siblings.size() == 0) return false;
        Style style = siblings.get(0).getStyle();
        Class<?> clazz = style.getClass();
        try {
            System.out.println(clazz.getField("bold").get(style));
            return true;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return false;
        }
    }

    public static MutableText setColor(MutableText component, Color color) {
        Style colorStyle = component.getStyle().withColor(TextColor.fromRgb(color.getRGB()));
        component.setStyle(colorStyle);
        return component;
    }


}
