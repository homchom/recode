package io.github.codeutilities.mixin.messages;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.events.ActionbarReceivedEvent;
import io.github.codeutilities.events.ChatReceivedEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinGameMessageListener {
    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        if (CodeUtilities.isOnDF() || !CodeUtilities.isOnDF()) {
            if (packet.getLocation() == MessageType.CHAT || packet.getLocation() == MessageType.SYSTEM) {
                ChatReceivedEvent.onMessage(packet.getMessage(), ci);
            }
            if (packet.getLocation() == MessageType.GAME_INFO) {
                ActionbarReceivedEvent.onMessage(packet.getMessage(), ci);
            }
        }
    }

    @Inject(method = "onTitle", at = @At("HEAD"), cancellable = true)
    private void onTitle(TitleS2CPacket packet, CallbackInfo ci) {
        TitleS2CPacket.Action action = packet.getAction();
        if (action == TitleS2CPacket.Action.ACTIONBAR) {
            ActionbarReceivedEvent.onMessage(packet.getText(), ci);
        }
    }
}
