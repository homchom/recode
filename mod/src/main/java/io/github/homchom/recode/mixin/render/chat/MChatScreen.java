package io.github.homchom.recode.mixin.render.chat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.homchom.recode.feature.social.DGuiWithSideChat;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatScreen.class)
public abstract class MChatScreen {
    @WrapOperation(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;getMessageTagAt(DD)Lnet/minecraft/client/GuiMessageTag;"
    ))
    private GuiMessageTag recognizeSideChatTags(
            ChatComponent mainChat,
            double screenX,
            double screenY,
            Operation<GuiMessageTag> operation
    ) {
        var mainTag = operation.call(mainChat, screenX, screenY);
        if (mainTag != null) return mainTag;
        var gui = (DGuiWithSideChat) Minecraft.getInstance().gui;
        return gui.getRecode$sideChat().getMessageTagAt(screenX, screenY);
    }
}
