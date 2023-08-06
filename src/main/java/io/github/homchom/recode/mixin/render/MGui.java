package io.github.homchom.recode.mixin.render;

import io.github.homchom.recode.feature.social.MCGuiWithSideChat;
import io.github.homchom.recode.feature.social.SideChat;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class MGui implements MCGuiWithSideChat {
    @Unique
    private final SideChat sideChat = new SideChat();

    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphics;III)V"
    ))
    private void renderSideChat(ChatComponent chat, GuiGraphics graphics, int tickDelta, int x, int y) {
        chat.render(graphics, tickDelta, x, y);
        sideChat.render(graphics, tickDelta, x, y);
    }

    @Inject(method = "tick()V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;tick()V",
            shift = At.Shift.AFTER
    ))
    private void tickSideChat(CallbackInfo ci) {
        sideChat.tick();
    }

    @Inject(method = "onDisconnected", at = @At("RETURN"))
    private void clearSideChatOnDisconnect(CallbackInfo ci) {
        sideChat.clearMessages(true);
    }

    @Unique
    @NotNull
    public SideChat recode$getSideChat() {
        return sideChat;
    }
}
