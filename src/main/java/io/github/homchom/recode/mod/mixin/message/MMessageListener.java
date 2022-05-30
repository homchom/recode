package io.github.homchom.recode.mod.mixin.message;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.event.*;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.Lagslayer;
import io.github.homchom.recode.mod.features.keybinds.FlightspeedToggle;
import io.github.homchom.recode.mod.features.social.chat.message.Message;
import io.github.homchom.recode.sys.networking.*;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import io.github.homchom.recode.sys.util.VersionUtil;
import kotlin.jvm.functions.Function1;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(ClientPacketListener.class)
public class MMessageListener {
    private static long lastPatchCheck = 0;
    private static long lastBuildCheck = 0;
    private final Minecraft minecraftClient = Minecraft.getInstance();
    private boolean motdShown = false;
    private final Function1<Message, EventResult> invoker =
            EventExtensions.getCall(RecodeEvents.RECEIVE_CHAT_MESSAGE);

    @Inject(method = "handleChat", at = @At("HEAD"), cancellable = true)
    private void handleChat(ClientboundChatPacket packet, CallbackInfo ci) {
        if (DFInfo.isOnDF()) {
            if (packet.getType() == ChatType.CHAT || packet.getType() == ChatType.SYSTEM) {
                if (RenderSystem.isOnRenderThread()) {
                    if (invoker.invoke(new Message(packet, ci)) == EventResult.FAILURE) ci.cancel();
                    try {
                        this.updateVersion(packet.getMessage());
                        this.updateState(packet.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Recode.error("Error while trying to parse the chat text!");
                    }
                }
            }
        }
    }

    private void updateVersion(Component component) {
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
                                minecraftClient.player.displayClientMessage(new TextComponent(string).withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))), false);
                            }

                            if (!Recode.BETA) {
                                int latestVersion = VersionUtil.getLatestVersion();
                                int currentVersion = VersionUtil.getCurrentVersionInt();
                                int versionsBehind = latestVersion - currentVersion;

                                if (versionsBehind > 10) {
                                    MutableComponent message = new TextComponent("")
                                            .append(new TextComponent(String.format("You are currently on build #%s of CodeUtilities, which is %s versions behind the latest (%s). ",
                                                    currentVersion, versionsBehind, latestVersion))
                                                    .withStyle(style -> style.withColor(ChatFormatting.YELLOW)))
                                            .append(new TextComponent("Click here to download the latest version!")
                                                    .withStyle(style -> {
                                                        style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://codeutilities.github.io"));
                                                        style.withColor(ChatFormatting.AQUA);
                                                        return style;
                                                    }));

                                    minecraftClient.player.displayClientMessage(message, false);

                                }
                            }

                        } catch (IOException ignored) {
                        }

                        motdShown = true;
                    }

                    DFInfo.isPatchNewer(patchText, "0"); //very lazy validation lol
                    DFInfo.patchId = patchText;
                    DFInfo.currentState.sendLocate();
                    Recode.info("DiamondFire Patch " + DFInfo.patchId + " detected!");

                    lastPatchCheck = time;

                    // update state on server join
                    DFInfo.currentState.setInSession(false);

                    // auto chat local
                    if (Config.getBoolean("autoChatLocal")) {
                        //Deprecated ChatUtil.executeCommandSilently("c 1");
                        ChatUtil.executeCommandSilently("chat local");
                    }
                }
            } catch (Exception e) {
                Recode.info("Error on parsing patch number!");
                e.printStackTrace();
            }
        }
    }

    private void updateState(Component component) {
        if (minecraftClient.player == null) return;

        String text = component.getString();

        // Flight speed
        if (text.matches("^Set flight speed to: \\d+% of default speed\\.$") && !text.matches("^Set flight speed to: 100% of default speed\\.$")) {
            FlightspeedToggle.fs_is_normal = false;
        }

        // Play Mode
        if (text.matches("^Joined game: .* by .*$")) {
            DFInfo.currentState.sendLocate();

            // Auto LagSlayer
            System.out.println(Lagslayer.lagSlayerEnabled);
            if (!Lagslayer.lagSlayerEnabled && Config.getBoolean("autolagslayer")) {
                ChatUtil.executeCommandSilently("lagslayer");
            }

            // fs toggle
            FlightspeedToggle.fs_is_normal = true;
        }

        // Enter Session
        if (text.matches("^You have entered a session with .*\\.$")) {
            if (!DFInfo.currentState.isInSession()) {
                DFInfo.currentState.setInSession(true);
                DFInfo.currentState.sendLocate();
            }
        }

        // End Session
        if (text.matches("^" + minecraftClient.player.getName().getContents() + " finished a session with .*\\. ▶ .*$")) {
            if (DFInfo.currentState.isInSession()) {
                DFInfo.currentState.setInSession(false);
                DFInfo.currentState.sendLocate();
            }
        }
        if (text.matches("^Your session with .* has ended\\.$")) {
            if (DFInfo.currentState.isInSession()) {
                DFInfo.currentState.setInSession(false);
                DFInfo.currentState.sendLocate();
            }
        }

        // Build Mode
        if (minecraftClient.player.isCreative() && text.matches("^» You are now in build mode\\.$")) {
            if (DFInfo.currentState.getMode() != State.Mode.BUILD) {
                DFInfo.currentState.sendLocate();
            }

            // Auto LagSlayer
            if (!Lagslayer.lagSlayerEnabled && Config.getBoolean("autolagslayer")) {
                ChatUtil.executeCommandSilently("lagslayer");
            }

            // fs toggle
            FlightspeedToggle.fs_is_normal = true;

            long time = System.currentTimeMillis() / 1000L;
            if (time - lastBuildCheck > 1) {
                new Thread(() -> {
                    try {
                        Thread.sleep(20);
                        if (Config.getBoolean("autotime")) {
                            ChatUtil.executeCommandSilently("time " + Config.getLong("autotimeval"));
                        }
                        if (Config.getBoolean("autonightvis")) {
                            ChatUtil.executeCommandSilently("nightvis");
                        }
                    } catch (Exception e) {
                        Recode.error("Error while executing the task!");
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

            new Thread(() -> {
                try {
                    Thread.sleep(10);
                    if (Config.getBoolean("autoRC")) {
                        Recode.MC.player.chat("/rc");
                    }
                    if (Config.getBoolean("autotime")) {
                        ChatUtil.executeCommandSilently("time " + Config.getLong("autotimeval"));
                    }
                    if (Config.getBoolean("autonightvis")) {
                        ChatUtil.executeCommandSilently("nightvis");
                    }
                } catch (Exception e) {
                    Recode.error("Error while executing the task!");
                    e.printStackTrace();
                }
            }).start();
        }
    }
}