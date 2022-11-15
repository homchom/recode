package io.github.homchom.recode.mod.mixin.message;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.LagslayerHUD;
import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessage;
import io.github.homchom.recode.server.Message;
import io.github.homchom.recode.server.MessageMatcher;
import io.github.homchom.recode.server.ReceiveChatMessageEvent;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import io.github.homchom.recode.util.LegacyCoroutineFunctions;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Pattern;

@Mixin(ClientPacketListener.class)
public class MMessageListener {
    private static long lastPatchCheck = 0;
    //private boolean motdShown = false;

    private final Pattern lsRegex = Pattern.compile("^CPU Usage: \\[▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮] \\(.*%\\)$");

    @Inject(method = "handleSystemChat", at = @At("HEAD"), cancellable = true)
    private void handleChat(ClientboundSystemChatPacket packet, CallbackInfo ci) {
        if (DFInfo.isOnDF() && RenderSystem.isOnRenderThread()) {
            // temporary, to preserve non-migrated side effects (like message grabbing)
            // TODO: remove after new message listener is complete
            new LegacyMessage(packet, ci);

            Lazy<Message> msg = LazyKt.lazy(() -> MessageMatcher.INSTANCE.match(packet.content()));
            if (!ReceiveChatMessageEvent.INSTANCE.run(new Pair<>(msg, packet.content()), true)) ci.cancel();
            try {
                this.updateVersion(packet.content());
                this.updateState(packet.content());
            } catch (Exception e) {
                e.printStackTrace();
                LegacyRecode.error("Error while trying to parse the chat text!");
            }
        }
    }

    @Inject(method = "setActionBarText", at = @At("HEAD"), cancellable = true)
    private void setActionBarText(ClientboundSetActionBarTextPacket packet, CallbackInfo ci) {
        if (Minecraft.getInstance().player == null) return;
        if (lsRegex.matcher(packet.getText().getString()).matches()) {
            if (Config.getBoolean("cpuOnScreen")) {
                LagslayerHUD.updateCPU(packet);
                ci.cancel();
            }
        }
    }

    private void updateVersion(Component component) {
        if (Minecraft.getInstance().player == null) return;

        String text = component.getString();

        if (text.matches("Current patch: .*\\. See the patch notes with /patch!")) {
            try {
                long time = System.currentTimeMillis() / 1000L;
                if (time - lastPatchCheck > 2) {
                    String patchText = text.replaceAll("Current patch: (.*)\\. See the patch notes with /patch!", "$1");

                    // TODO: reimplement?
                    /*if (!motdShown) {
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
                    }*/

                    DFInfo.isPatchNewer(patchText, "0"); //very lazy validation lol
                    DFInfo.patchId = patchText;
                    DFInfo.currentState.sendLocate();
                    LegacyRecode.info("DiamondFire Patch " + DFInfo.patchId + " detected!");

                    lastPatchCheck = time;

                    // update state on server join
                    DFInfo.currentState.setInSession(false);
                }
            } catch (Exception e) {
                LegacyRecode.info("Error on parsing patch number!");
                e.printStackTrace();
            }
        }
    }

    private void updateState(Component component) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        String text = component.getString();

        // Enter Session
        if (text.matches("^\\[SUPPORT] " + player.getName().getString() + " entered a session with \\w+\\. ▶ \\S+ \\S+!?$")) {
            if (!DFInfo.currentState.isInSession()) {
                new Thread(() -> {
                    try {
                        Thread.sleep(1300);
                        if (DFInfo.currentState.getMode() != LegacyState.Mode.DEV) {
                            DFInfo.currentState.sendLocate();
                        }
                        Thread.sleep(200);
                        if (DFInfo.currentState.getMode() == LegacyState.Mode.DEV) {
                            DFInfo.currentState.setInSession(true);
                        }
                    } catch(Exception e){
                        LegacyRecode.error("Error while executing the task!");
                        e.printStackTrace();
                    }
                }).start();
            }
        }

        // End Session
        if (text.matches("^\\[SUPPORT] " + player.getName().getString() + " finished a session with \\w+\\. ▶ \\d+:\\d+:\\d+$") || text.matches("^\\[SUPPORT] " + player.getName().getString() + " terminated a session with \\w+\\. ▶ \\d+:\\d+:\\d+$") || text.matches("\\[SUPPORT\\] \\w+ left a session with " + player.getName().getString() + ".$")) {
            if (DFInfo.currentState.isInSession()) {
                DFInfo.currentState.setInSession(false);
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        DFInfo.currentState.sendLocate();
                    } catch(Exception e){
                        LegacyRecode.error("Error while executing the task!");
                        e.printStackTrace();
                    }
                }).start();
            }
        }

        // Play Mode
        if (text.matches("^» Joined game: .* by .*$")) {
            LegacyCoroutineFunctions.checkTwiceForMode(LegacyState.Mode.PLAY, () -> {
                if (Config.getBoolean("autoChatLocal") && !DFInfo.currentState.isInSession()) {
                    ChatUtil.executeCommandSilently("chat local");
                }
            });
        }

        // Build Mode
        if (text.matches("^» You are now in build mode\\.$")) {
            LegacyCoroutineFunctions.checkTwiceForMode(LegacyState.Mode.BUILD, () -> {
                if (Config.getBoolean("autotime")) {
                    ChatUtil.executeCommandSilently("time " + Config.getLong("autotimeval"));
                }
                if (Config.getBoolean("autonightvis")) {
                    ChatUtil.executeCommandSilently("nightvis");
                }
                if (Config.getBoolean("autoChatLocal") && !DFInfo.currentState.isInSession()) {
                    ChatUtil.executeCommandSilently("chat local");
                }
            });
        }

        if (text.matches("^» You are now in dev mode\\.$")) {
            LegacyCoroutineFunctions.checkTwiceForMode(LegacyState.Mode.DEV, () -> {
                if (Config.getBoolean("autoRC")) {
                    ChatUtil.executeCommand("resetcompact");
                }
                if (Config.getBoolean("autotime")) {
                    ChatUtil.executeCommandSilently("time " + Config.getLong("autotimeval"));
                }
                if (Config.getBoolean("autonightvis")) {
                    ChatUtil.executeCommandSilently("nightvis");
                }
                if (Config.getBoolean("autoChatLocal") && !DFInfo.currentState.isInSession()) {
                    ChatUtil.executeCommandSilently("chat local");
                }
                if (!LagslayerHUD.lagSlayerEnabled && Config.getBoolean("autolagslayer")) {
                    ChatUtil.executeCommandSilently("lagslayer");
                }
            });
        }
    }
}