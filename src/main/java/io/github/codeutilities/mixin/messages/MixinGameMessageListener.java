package io.github.codeutilities.mixin.messages;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.CodeUtilsConfig;
import io.github.codeutilities.events.interfaces.ChatEvents;
import io.github.codeutilities.events.register.ReceiveChatMessageEvent;
import io.github.codeutilities.features.external.DFDiscordRPC;
import io.github.codeutilities.features.keybinds.FlightspeedToggle;
import io.github.codeutilities.gui.CPU_UsageText;
import io.github.codeutilities.util.chat.MessageGrabber;
import io.github.codeutilities.util.networking.DFInfo;
import io.github.codeutilities.util.networking.WebUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinGameMessageListener {
    private static long lastPatchCheck = 0;
    private static long lastBuildCheck = 0;
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private boolean motdShown = false;
    private final ChatEvents invoker = ChatEvents.RECEIVE_MESSAGE.invoker();

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        if(MessageGrabber.isActive()) {
            MessageGrabber.supply(packet.getMessage());

            if(MessageGrabber.isSilent()) {
                ci.cancel();
                CodeUtilities.log(Level.INFO, "[CANCELLED] " + packet.getMessage().getString());
            }
        }
        if (DFInfo.isOnDF()) {
            if (packet.getLocation() == MessageType.CHAT || packet.getLocation() == MessageType.SYSTEM) {
                if (RenderSystem.isOnRenderThread()) {
                    if (invoker.receive(packet.getMessage()).equals(ActionResult.SUCCESS)) ci.cancel();
                    String text = packet.getMessage().getString();
                    try {
                        this.updateVersion(packet.getMessage());
                        this.updateState(packet.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        CodeUtilities.log(Level.ERROR, "Error while trying to parse the chat text!");
                    }
                }
            }
        }
    }

    @Inject(method = "onTitle", at = @At("HEAD"), cancellable = true)
    private void onTitle(TitleS2CPacket packet, CallbackInfo ci) {
        TitleS2CPacket.Action action = packet.getAction();
        if (minecraftClient.player == null) return;
        if (action == TitleS2CPacket.Action.ACTIONBAR) {
            if (packet.getText().getString().matches("^CPU Usage: \\[▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮\\] \\(.*%\\)$")) {
                if (CodeUtilsConfig.getBoolean("cpuOnScreen")) {
                    CPU_UsageText.updateCPU(packet);
                    ci.cancel();
                }
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

                    if (!motdShown) {
                        try {
                            String str = WebUtil.getString("https://codeutilities.github.io/data/motd.txt");
                            for (String string : str.split("\n")) {
                                minecraftClient.player.sendMessage(new LiteralText(string).styled(style -> style.withColor(TextColor.fromFormatting(Formatting.AQUA))), false);
                            }

                            String version = WebUtil.getString("https://codeutilities.github.io/data/currentversion.txt").replaceAll("\n", "");
                            if (!CodeUtilities.MOD_VERSION.equals(version) && !CodeUtilities.BETA) {
                                minecraftClient.player.sendMessage(new LiteralText(String.format("A new version of CodeUtilities (%s) is available! Click here to download!", version)).styled(style ->
                                        style.withColor(TextColor.fromFormatting(Formatting.YELLOW))).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://codeutilities.github.io/"))), false);
                            }

                        } catch (IOException ignored) {
                        }

                        motdShown = true;
                    }

                    DFInfo.isPatchNewer(patchText, "0"); //very lazy validation lol
                    DFInfo.patchId = patchText;
                    DFInfo.currentState = null;
                    CodeUtilities.log(Level.INFO, "DiamondFire Patch " + DFInfo.patchId + " detected!");

                    lastPatchCheck = time;

                    // update rpc on server join
                    DFDiscordRPC.delayRPC = true;
                    DFDiscordRPC.supportSession = false;

                    // auto chat local
                    if (CodeUtilsConfig.getBoolean("autoChatLocal")) {
                        minecraftClient.player.sendChatMessage("/c 1");
                        ReceiveChatMessageEvent.cancelMsgs = 1;
                    }
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
        if (text.matches("^Joined game: .* by .*$")) {
            DFInfo.currentState = DFInfo.State.PLAY;

            // Auto LagSlayer
            System.out.println(CPU_UsageText.lagSlayerEnabled);
            if (!CPU_UsageText.lagSlayerEnabled && CodeUtilsConfig.getBoolean("autolagslayer")) {
                minecraftClient.player.sendChatMessage("/lagslayer");
                ReceiveChatMessageEvent.cancelLagSlayerMsg = true;
            }

            // fs toggle
            FlightspeedToggle.fs_is_normal = true;
        }

        // Enter Session
        if (text.matches("^You have entered a session with .*\\.$")) {
            if (!DFDiscordRPC.supportSession) {
                DFDiscordRPC.supportSession = true;
                if (CodeUtilsConfig.getBoolean("discordRPC")) {
                    new Thread(() -> {
                        DFDiscordRPC.getInstance().getThread().locateRequest();
                    }).start();
                }
            }
        }

        // End Session
        if (text.matches("^" + minecraftClient.player.getName().asString() + " finished a session with .*\\. ▶ .*$")) {
            if (DFDiscordRPC.supportSession) {
                DFDiscordRPC.supportSession = false;
                if (CodeUtilsConfig.getBoolean("discordRPC")) {
                    new Thread(() -> {
                        DFDiscordRPC.getInstance().getThread().locateRequest();
                    }).start();
                }
            }
        }
        if (text.matches("^Your session with .* has ended\\.$")) {
            if (DFDiscordRPC.supportSession) {
                DFDiscordRPC.supportSession = false;
                if (CodeUtilsConfig.getBoolean("discordRPC")) {
                    new Thread(() -> {
                        DFDiscordRPC.getInstance().getThread().locateRequest();
                    }).start();
                }
            }
        }

        // Build Mode
        if (minecraftClient.player.isCreative() && text.matches("^» You are now in build mode\\.$")) {
            if (DFInfo.currentState != DFInfo.State.BUILD) {
                DFInfo.currentState = DFInfo.State.BUILD;
            }

            // Auto LagSlayer
            if (!CPU_UsageText.lagSlayerEnabled && CodeUtilsConfig.getBoolean("autolagslayer")) {
                minecraftClient.player.sendChatMessage("/lagslayer");
                ReceiveChatMessageEvent.cancelLagSlayerMsg = true;
            }

            // fs toggle
            FlightspeedToggle.fs_is_normal = true;

            long time = System.currentTimeMillis() / 1000L;
            if (time - lastBuildCheck > 1) {
                new Thread(() -> {
                    try {
                        Thread.sleep(20);
                        if (CodeUtilsConfig.getBoolean("autotime")) {
                            minecraftClient.player.sendChatMessage("/time " + CodeUtilsConfig.getInteger("autotimeval"));
                            ReceiveChatMessageEvent.cancelTimeMsg = true;
                        }
                        if (CodeUtilsConfig.getBoolean("autonightvis")) {
                            minecraftClient.player.sendChatMessage("/nightvis");
                            ReceiveChatMessageEvent.cancelNVisionMsg = true;
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
        if (minecraftClient.player.isCreative() && text.matches("^» You are now in dev mode\\.$")) {
            // fs toggle
            FlightspeedToggle.fs_is_normal = true;
        }
    }
}