package io.github.codeutilities.mixin.messages;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.dfrpc.DFDiscordRPC;
import io.github.codeutilities.events.ChatReceivedEvent;
import io.github.codeutilities.gui.CPU_UsageText;
import io.github.codeutilities.keybinds.FlightspeedToggle;
import io.github.codeutilities.util.DFInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinGameMessageListener {
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        if (DFInfo.isOnDF()) {
            if (packet.getLocation() == MessageType.CHAT || packet.getLocation() == MessageType.SYSTEM) {
                ChatReceivedEvent.onMessage(packet.getMessage(), ci);
                String text = packet.getMessage().getString();
                try {
                    this.updateVersion(packet.getMessage());
                    this.updateState(packet.getMessage());
                } catch (Exception e) {
                    CodeUtilities.log(Level.ERROR, "Error while trying to parse the chat text!");
                }
            }
        }
    }

    @Inject(method = "onTitle", at = @At("HEAD"), cancellable = true)
    private void onTitle(TitleS2CPacket packet, CallbackInfo ci) {
        TitleS2CPacket.Action action = packet.getAction();
        if (minecraftClient.player == null) return;
        if (action == TitleS2CPacket.Action.ACTIONBAR) {
            if (packet.getText().getString().equals("CPU Usage: [▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮]")) {
                if (ModConfig.getConfig().cpuOnScreen) {
                    CPU_UsageText.updateCPU(packet);
                    ci.cancel();
                }
            } else if (packet.getText().getString().matches("DiamondFire - .* .* CP - .* Tokens")) {
                if (DFInfo.currentState != DFInfo.State.LOBBY && ModConfig.getConfig().autofly) {
                    minecraftClient.player.sendChatMessage("/fly");
                    ChatReceivedEvent.cancelFlyMsg = true;
                }
                DFInfo.currentState = DFInfo.State.LOBBY;

                // fs toggle
                FlightspeedToggle.fs_is_normal = true;
            }
        }
    }

    private void updateVersion(Text component) {
        if (minecraftClient.player == null) return;

        String text = component.getString();

        if (text.matches("Current patch: .*\\. See the patch notes with /patch!")) {
            try {
                long time = System.currentTimeMillis() / 1000L;
                if (time - lastPatchCheck > 2) {
                    String patchText = text.replaceAll("Current patch: (.*)\\. See the patch notes with /patch!", "$1");

                    DFInfo.isPatchNewer(patchText, "0"); //very lazy validation lol
                    DFInfo.patchId = patchText;
                    DFInfo.currentState = null;
                    CodeUtilities.log(Level.INFO, "DiamondFire Patch " + DFInfo.patchId + " detected!");

                    lastPatchCheck = time;

                    // update rpc on server join
                    DFDiscordRPC.delayRPC = true;
                }
            } catch (Exception e) {
                CodeUtilities.log(Level.INFO, "Error on parsing patch number!");
                e.printStackTrace();
            }
        }
    }

    private void updateState(Text component) {
        if (minecraftClient.player == null) return;

        String text = component.getString();

        // Flight speed
        if (text.matches("^Set flight speed to: \\d+% of default speed\\.$") && !text.matches("^Set flight speed to: 100% of default speed\\.$")) {
            FlightspeedToggle.fs_is_normal = false;
        }

        // Play Mode
        if (text.matches("Joined game: .* by .*") && text.startsWith("Joined game: ")) {
            DFInfo.currentState = DFInfo.State.PLAY;

            // fs toggle
            FlightspeedToggle.fs_is_normal = true;
        }

        // Build Mode
        if (minecraftClient.player.isCreative() && text.contains("» You are now in build mode.") && text.startsWith("»")) {
            if (DFInfo.currentState != DFInfo.State.BUILD) {
                DFInfo.currentState = DFInfo.State.BUILD;
            }

            // fs toggle
            FlightspeedToggle.fs_is_normal = true;

            long time = System.currentTimeMillis() / 1000L;
            if (time - lastBuildCheck > 1) {
                new Thread(() -> {
                    try {
                        Thread.sleep(20);
                        if (ModConfig.getConfig().autotime) {
                            minecraftClient.player.sendChatMessage("/time " + ModConfig.getConfig().autotimeval);
                            ChatReceivedEvent.cancelTimeMsg = true;
                        }
                        if (ModConfig.getConfig().autonightvis) {
                            minecraftClient.player.sendChatMessage("/nightvis");
                            ChatReceivedEvent.cancelNVisionMsg = true;
                        }
                    } catch (Exception e) {
                        CodeUtilities.log(Level.ERROR, "Error while executing the task!");
                        e.printStackTrace();
                    }
                }).start();

                lastBuildCheck = time;
            }
        }

        // Dev Mode (more moved to MixinItemSlotUpdate)
        if (minecraftClient.player.isCreative() && text.contains("» You are now in dev mode.") && text.startsWith("»")) {
            // fs toggle
            FlightspeedToggle.fs_is_normal = true;
        }
    }

    private static long lastPatchCheck = 0;
    private static long lastBuildCheck = 0;
}