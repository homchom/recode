package io.github.codeutilities.mixin.packet;

import io.github.codeutilities.modules.triggers.Trigger;
import io.github.codeutilities.modules.triggers.impl.StateChangeTrigger;
import io.github.codeutilities.util.networking.DFInfo;
import io.github.codeutilities.util.networking.DFInfo.State;
import io.github.codeutilities.util.networking.TPSUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinTimeUpdateListener {

    State oldState = null;
    State newState = null;
    String oldStateName;
    String newStateName;

    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"), cancellable = true)
    private void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        TPSUtil.calculateTps(System.currentTimeMillis());

        // StateChangeTrigger
        newState = DFInfo.currentState;

        if (oldState == null) oldStateName = "null"; else oldStateName = oldState.getName();
        if (newState == null) newStateName = "null"; else newStateName = newState.getName();

        if (newState != oldState) {
            oldState = newState;
            Trigger.execute(new StateChangeTrigger(), oldStateName, newStateName);
        }

        oldState = newState;

        if (!DFInfo.isOnDF()) DFInfo.currentState = null;
    }
}
