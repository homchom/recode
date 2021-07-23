package io.github.codeutilities.sys.streamer;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.mod.events.impl.ReceiveSoundEvent;
import io.github.codeutilities.sys.util.chat.MessageGrabber;
import io.github.codeutilities.sys.util.chat.TextUtil;
import io.github.codeutilities.sys.util.networking.State;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class StreamerModeHandler {

    public static boolean get(String key) {
        return Config.getBoolean(key);
    }

    public static String getString(String key) {
        return Config.getString(key);
    }

    public static boolean enabled() {
        return get("streamer");
    }

    public static boolean getOption(String key) {
        return enabled() && get(key);
    }

    public static boolean autoAdminV() {
        return getOption("streamerAutoAdminV");
    }

    public static boolean autoChatLocal() {
        return getOption("streamerAutoChatLocal");
    }

    public static boolean spies() {
        return getOption("streamerSpies");
    }

    public static boolean hideSupport() {
        return getOption("streamerHideSupport");
    }

    public static boolean hideModeration() {
        return getOption("streamerHideModeration");
    }

    public static boolean hideAdmin() {
        return getOption("streamerHideAdmin");
    }

    public static boolean hideDMs() {
        return getOption("streamerHideDMs");
    }

    public static boolean hidePlotAds() {
        return getOption("streamerHidePlotAds");
    }

    public static boolean hideBuycraftUpdate() {
        return getOption("streamerHideBuycraftUpdate");
    }

    public static boolean hideRegexEnabled() {
        return getOption("streamerHideRegexEnabled");
    }

    public static String hideRegex() {
        return getString("streamerHideRegex");
    }

    private static final String SUPPORT_QUESTION_REGEX = "^.*» Support Question: \\(Click to answer\\)\\nAsked by \\w+ \\[[a-zA-Z]+]\\n.+$";
    private static final String SUPPORT_ANSWER_REGEX = "^.*\\n» \\w+ has answered \\w+'s question:\\n\\n.+\\n.*$";
    private static final String BUYCRAFT_UPDATE_REGEX = "^A new version of BuycraftX \\([0-9.]+\\) is available\\. Go to your server panel at https://server.tebex.io/plugins to download the update\\.$";
    private static final String PLOT_AD_REGEX = "^.*\\[ Plot Ad ].*\\n.+\\n.*$";
    private static final String SCANNING_REGEX = "^Scanning \\w+(.|\n)*\\[Online] \\[Offline] \\[(IP|)Banned]\1*$";

    private static String getDmRegex(String sender) {
        return "^\\[" + sender + " → You] .+$";
    }

    public static boolean handleMessage(Text message) {
        if (!enabled()) return false;

        String colorCodes = TextUtil.textComponentToColorCodes(message);
        String stripped = message.getString();

        // Hide support messages
        if (hideSupport()) {
            // General support messages (Broadcast, session requests and completion, etc.)
            if (stripped.startsWith("[SUPPORT]")) {
                return true;
            }

            // Support question and answer
            if (stripped.matches(SUPPORT_QUESTION_REGEX) ||
                    stripped.matches(SUPPORT_ANSWER_REGEX)) {
                ReceiveSoundEvent.cancelNextSound();
                return true;
            }
        }

        // Hide moderation messages
        if (hideModeration() && (
                // General moderation messages (Broadcast, AntiX, etc.)
                stripped.startsWith("[MOD]") ||
                // Silent punishments
                stripped.startsWith("[Silent]") ||
                // Incoming reports
                stripped.startsWith("! Incoming Report ") ||
                // Scanning
                stripped.matches(SCANNING_REGEX)
                )) {
            return true;
        }

        // Hide admin messages
        if (hideAdmin() && (
                // General admin messages (Broadcast, etc.)
                stripped.startsWith("[ADMIN]")
                )) {
            return true;
        }

        // Hide spies (Session spy, Muted spy, DM spy)
        if (spies() && stripped.startsWith("* ")) {
            return true;
        }

        // Hide DMs (and don't match if it's ryanland ;])
        if (hideDMs() && (stripped.matches(getDmRegex("\\w+"))) && !stripped.matches(getDmRegex("RyanLand"))) {
            ReceiveSoundEvent.cancelNextSound();
            return true;
        }

        // Hide Plot Ads
        if (hidePlotAds() && stripped.matches(PLOT_AD_REGEX)) {
            ReceiveSoundEvent.cancelNextSound();
            return true;
        }

        // Hide Buycraft Update
        if (hideBuycraftUpdate() && stripped.matches(BUYCRAFT_UPDATE_REGEX)) {
            return true;
        }

        // Hide messages matching regex
        if (hideRegexEnabled() && stripped.matches(hideRegex())) {
            return true;
        }

        return false;
    }

    // Only triggers when joining DF or switching nodes
    public static void handleServerJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        if (!enabled()) return;

        // Run "/adminv off" and hide the message
        if (autoAdminV()) {
            CodeUtilities.MC.player.sendChatMessage("/adminv off");
        }

        // Run "/chat local" and hide the message
        if (autoChatLocal()) {
            CodeUtilities.MC.player.sendChatMessage("/c l");
        }

        // Hide messages
        if (autoAdminV() ^ autoChatLocal()) {
            MessageGrabber.hideSilently(1);
        } else if (autoAdminV() && autoChatLocal()) {
            MessageGrabber.hideSilently(2);
        }
    }

    public static void handleStateChange(State oldState, State newState) {
        if (!enabled()) return;

        // If the state is changed to mode play, run "/chat local"
        // Note: May trigger simultaneously with StreamerHandler#handleServerJoin, but this is not a problem
        if (autoChatLocal() && newState.mode.equals(State.Mode.PLAY)) {
            CodeUtilities.MC.player.sendChatMessage("/c l");
            MessageGrabber.hideSilently(1);
        }
    }
}
