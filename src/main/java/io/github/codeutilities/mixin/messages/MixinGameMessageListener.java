package io.github.codeutilities.mixin.messages;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.events.ChatReceivedEvent;
import io.github.codeutilities.util.DFInfo;
import net.minecraft.client.MinecraftClient;
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
    private MinecraftClient minecraftClient = MinecraftClient.getInstance();

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        if (CodeUtilities.isOnDF()) {
            if (packet.getLocation() == MessageType.CHAT || packet.getLocation() == MessageType.SYSTEM) {
                ChatReceivedEvent.onMessage(packet.getMessage(), ci);

                String text = packet.getMessage().getString();

                updateVersion(text);
                updateState(text);
            }

            if (packet.getLocation() == MessageType.GAME_INFO) {
                if (packet.getMessage().getString().matches("DiamondFire  - .* CP - ⛁ .* Credits")) {
                    DFInfo.currentState = DFInfo.State.LOBBY;
                }
            }
        }
    }

    @Inject(method = "onTitle", at = @At("HEAD"), cancellable = true)
    private void onTitle(TitleS2CPacket packet, CallbackInfo ci) {
        TitleS2CPacket.Action action = packet.getAction();
        if (action == TitleS2CPacket.Action.ACTIONBAR) {
            if (packet.getText().getString().matches("DiamondFire  - .* CP - ⛁ .* Credits")) {
                DFInfo.currentState = DFInfo.State.LOBBY;
            }
        }
    }

    private void updateVersion(String text) {
        if (text.matches("Current patch: .*\\. See the patch notes with \\/patch!")) {
            try {
                String patchText = text.replaceAll("Current patch: (.*)\\. See the patch notes with \\/patch!", "$1");

                DFInfo.isPatchNewer(patchText, "0"); //very lazy validation lol
                DFInfo.patchId = patchText;
                CodeUtilities.log(Level.INFO, "DiamondFire Patch " + DFInfo.patchId + " detected!");
            }catch (Exception e) {
                CodeUtilities.log(Level.INFO, "Error on parsing patch number!");
                e.printStackTrace();
            }
        }
    }

    private void updateState(String text) {
        // Play Mode
        if (text.matches("Joined game: .* by .*") && text.startsWith("Joined game: ")) {
            DFInfo.currentState = DFInfo.State.PLAY;
        }

        // Build Mode
        if (minecraftClient.player.isCreative() && text.contains("» You are now in build mode.") && text.startsWith("»")) {
            DFInfo.currentState = DFInfo.State.BUILD;
        }

        // Dev Mode
        if (minecraftClient.player.isCreative() && text.contains("» You are now in dev mode.") && text.startsWith("»")) {
            DFInfo.currentState = DFInfo.State.DEV;
        }
    }
}
