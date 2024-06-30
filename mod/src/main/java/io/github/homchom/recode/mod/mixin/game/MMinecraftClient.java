package io.github.homchom.recode.mod.mixin.game;

import io.github.homchom.recode.sys.sidedchat.ChatShortcut;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MMinecraftClient {
    @Inject(method = "openChatScreen", at = @At("HEAD"))
    public void openChatScreen(String text, CallbackInfo ci) {
        // set such that no shortcut is active when pressing 't'
        ChatShortcut.setCurrentChatShortcut(null);
    }
}
