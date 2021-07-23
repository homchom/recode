package io.github.codeutilities.mod.mixin.game;

import blue.endless.jankson.annotation.Nullable;
import io.github.codeutilities.sys.player.DFInfo;
import io.github.codeutilities.sys.networking.State;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MClient {

    /**
     * @author CodeUtilities
     * @reason yea
     */
    @Inject(method = "openScreen(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"), cancellable = true)
    public void openScreen(@Nullable Screen screen, CallbackInfo cbi) {
        if(MinecraftClient.getInstance().player == null) {
            DFInfo.currentState.setMode(State.Mode.OFFLINE);
        }
    }

}
