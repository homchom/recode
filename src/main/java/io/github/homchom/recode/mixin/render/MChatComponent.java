package io.github.homchom.recode.mixin.render;

import io.github.homchom.recode.feature.social.MCGuiWithSideChat;
import io.github.homchom.recode.feature.social.SideChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.ChatListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public abstract class MChatComponent {
    // side chat "overrides" for private members

    @ModifyVariable(method = "screenToChatX", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double overrideScreenToChatX(double screenX) {
        var sideChat = asSideChatOrNull();
        if (sideChat != null) screenX -= sideChat.getXOffset();
        return screenX;
    }

    // side chat redirects

    // TODO: improve handling of chat queue (partition it?)
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/chat/ChatListener;queueSize()J"
    ))
    private long redirectMessageQueueSize(ChatListener listener) {
        return isSideChat() ? 0 : listener.queueSize();
    }

    // actions synced between main chat and side chat

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickSideChat(CallbackInfo ci) {
        if (isMainChat()) getSideChat().tick();
    }

    @Inject(method = "scrollChat", at = @At("HEAD"))
    private void scrollSideChat(int amount, CallbackInfo ci) {
        if (isMainChat()) getSideChat().scrollChat(amount);
    }

    @Inject(method = "clearMessages", at = @At("HEAD"))
    private void clearSideChat(boolean refresh, CallbackInfo ci) {
        if (isMainChat()) getSideChat().clearMessages(refresh);
    }

    // side chat helper methods

    @Unique
    private SideChat getSideChat() {
        var gui = (MCGuiWithSideChat) Minecraft.getInstance().gui;
        return gui.recode$getSideChat();
    }

    @Unique
    private boolean isMainChat() {
        var instance = (ChatComponent) (Object) this;
        return instance == Minecraft.getInstance().gui.getChat();
    }

    @Unique
    private boolean isSideChat() {
        var instance = (ChatComponent) (Object) this;
        return instance instanceof SideChat;
    }

    @Unique
    private @Nullable SideChat asSideChatOrNull() {
        var instance = (ChatComponent) (Object) this;
        return instance instanceof SideChat sideChat ? sideChat : null;
    }
}
