package io.github.homchom.recode.mixin.render;

import io.github.homchom.recode.feature.social.SideChat;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.ChatListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatComponent.class)
public class MChatComponent {
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

    // side chat "overrides" for private members

    @Inject(method = "screenToChatX", at = @At("RETURN"), cancellable = true)
    private void overrideScreenToChatX(double screenX, CallbackInfoReturnable<Double> cir) {
        var sideChat = asSideChatOrNull();
        if (sideChat != null) cir.setReturnValue(cir.getReturnValue() - sideChat.getXOffset());
    }

    // side chat redirects

    // TODO: improve handling of chat queue (partition it?)
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/chat/ChatListener;queueSize()J"
    ))
    private long redirectMessageQueueSize(ChatListener listener) {
        return isSideChat() ? 0 : listener.queueSize();
    }
}
