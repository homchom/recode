package io.github.homchom.recode.mixin.render;

import io.github.homchom.recode.feature.social.MCGuiWithSideChat;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatScreen.class)
public class MChatScreen {
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;getMessageTagAt(DD)Lnet/minecraft/client/GuiMessageTag;"
    ))
    private GuiMessageTag recognizeSideChatTags(ChatComponent mainChat, double screenX, double screenY) {
        var mainTag = mainChat.getMessageTagAt(screenX, screenY);
        if (mainTag != null) return mainTag;
        var gui = (MCGuiWithSideChat) Minecraft.getInstance().gui;
        return gui.recode$getSideChat().getMessageTagAt(screenX, screenY);
    }
}
