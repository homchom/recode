package io.github.homchom.recode.mixin.render;

import io.github.homchom.recode.feature.social.MCGuiWithSideChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatScreen.class)
public class MChatScreen {
    @Redirect(method = { "mouseScrolled", "keyPressed" }, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;scrollChat(I)V"
    ))
    private void scrollSideChat(ChatComponent chat, int amount) {
        chat.scrollChat(amount);
        var gui = (MCGuiWithSideChat) Minecraft.getInstance().gui;
        gui.recode$getSideChat().scrollChat(amount);
    }
}
