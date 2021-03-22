package io.github.codeutilities.util;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.*;
import net.minecraft.text.*;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class ChatUtil {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

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
        if (chatType == null) {
            mc.player.sendMessage(text, false);
        } else {
            ChatUtil.setColor(text, MinecraftColors.fromCode(chatType.getTrailing()).getColor());
            mc.player.sendMessage(new LiteralText(chatType.getString() + " ").append(text), false);
            if (chatType == ChatType.FAIL) {
                if (ModConfig.getConfig().errorSound) {
                    MinecraftClient.getInstance().player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 2, 0);
                }
            }
        }
    }
    
    public static MutableText setColor(MutableText component, Color color) {
        Style colorStyle = component.getStyle().withColor(TextColor.fromRgb(color.getRGB()));
        component.setStyle(colorStyle);
        return component;
    }


}
