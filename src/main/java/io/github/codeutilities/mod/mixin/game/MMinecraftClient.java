package io.github.codeutilities.mod.mixin.game;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.sys.sidedchat.ChatShortcut;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MMinecraftClient {

    @Inject(method = "stop", at = @At("HEAD"))
    public void stop(CallbackInfo ci) {
        CodeUtilities.onClose();
    }

    @Inject(method = "openChatScreen", at = @At("HEAD"))
    public void openChatScreen(String text, CallbackInfo ci) {
        // set such that no shortcut is active when pressing 't'
        ChatShortcut.setCurrentChatShortcut(null);
    }
}
