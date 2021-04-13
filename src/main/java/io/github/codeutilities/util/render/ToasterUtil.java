package io.github.codeutilities.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

public class ToasterUtil {

    public static void sendToaster(String title, String description, SystemToast.Type type) {
        sendToaster(new LiteralText(title), new LiteralText(description), type);
    }

    public static void sendTranslateToaster(String titleIdentifier, String descIdentifier, SystemToast.Type type) {
        sendToaster(new TranslatableText(titleIdentifier), new TranslatableText(descIdentifier), type);
    }

    public static void sendToaster(MutableText title, MutableText description, SystemToast.Type type) {
        MinecraftClient.getInstance().getToastManager().add(new SystemToast(type, title, description));
    }

}
