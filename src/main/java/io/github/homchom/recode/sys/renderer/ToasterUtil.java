package io.github.homchom.recode.sys.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ToasterUtil {

    public static void sendToaster(String title, String description, SystemToast.SystemToastId type) {
        sendToaster(Component.literal(title), Component.literal(description), type);
    }

    public static void sendToaster(MutableComponent title, MutableComponent description, SystemToast.SystemToastId type) {
        Minecraft.getInstance().getToasts().addToast(new SystemToast(type, title, description));
    }

}
