package io.github.homchom.recode.mod.mixin.render;

import io.github.homchom.recode.sys.sidedchat.ChatShortcut;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ChatScreen.class)
public class MChatScreen {
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"), index = 4)
    private int getTextboxColor(int defaultColour) {
        ChatShortcut currentChatShortcut = ChatShortcut.getCurrentChatShortcut();

        // if there is one active - use it
        if (currentChatShortcut != null) {
            return currentChatShortcut.getColor().getRGB();
        }
        // else use the default minecraft option
        else return defaultColour;
    }

    @ModifyArg(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ChatScreen;handleChatInput(Ljava/lang/String;Z)Z"), index = 0)
    private String insertPrefix(String interceptedMessage) {
        ChatShortcut currentChatShortcut = ChatShortcut.getCurrentChatShortcut();

        if (currentChatShortcut != null) {
            // the prefix already includes the space
            return currentChatShortcut.getPrefix() + interceptedMessage;
        }
        // else just send the message
        else return interceptedMessage;
    }
}
