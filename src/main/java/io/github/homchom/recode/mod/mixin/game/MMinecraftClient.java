package io.github.homchom.recode.mod.mixin.game;

import io.github.homchom.recode.Recode;
import io.github.homchom.recode.sys.sidedchat.ChatShortcut;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ALL")
@Mixin(Minecraft.class)
public class MMinecraftClient {
    @Inject(method = "stop", at = @At("HEAD"))
    public void stop(CallbackInfo ci) {
        Recode.onClose();
    }

    @Inject(method = "openChatScreen", at = @At("HEAD"))
    public void openChatScreen(String text, CallbackInfo ci) {
        // set such that no shortcut is active when pressing 't'
        ChatShortcut.setCurrentChatShortcut(null);
    }
}
