package io.github.homchom.recode.sys.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.*;

public class ToasterUtil {

    public static void sendToaster(String title, String description, SystemToast.SystemToastIds type) {
        sendToaster(Component.literal(title), Component.literal(description), type);
    }

    public static void sendTranslateToaster(String titleIdentifier, String descIdentifier, SystemToast.SystemToastIds type) {
        sendToaster(Component.translatable(titleIdentifier), Component.translatable(descIdentifier), type);
    }

    public static void sendToaster(MutableComponent title, MutableComponent description, SystemToast.SystemToastIds type) {
        Minecraft.getInstance().getToasts().addToast(new SystemToast(type, title, description));
    }

}
