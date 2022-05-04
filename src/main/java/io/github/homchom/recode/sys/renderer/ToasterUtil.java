package io.github.homchom.recode.sys.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.*;

public class ToasterUtil {

    public static void sendToaster(String title, String description, SystemToast.SystemToastIds type) {
        sendToaster(new TextComponent(title), new TextComponent(description), type);
    }

    public static void sendTranslateToaster(String titleIdentifier, String descIdentifier, SystemToast.SystemToastIds type) {
        sendToaster(new TranslatableComponent(titleIdentifier), new TranslatableComponent(descIdentifier), type);
    }

    public static void sendToaster(MutableComponent title, MutableComponent description, SystemToast.SystemToastIds type) {
        Minecraft.getInstance().getToasts().addToast(new SystemToast(type, title, description));
    }

}
