package io.github.homchom.recode.mod.features.streamer;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.chat.MessageGrabber;

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

    public static boolean hideSpies() {
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

    // Looking for #handleMessage? This has been moved to mod.features.social.chat.message

    // Only triggers when joining DF or switching nodes
    public static void handleServerJoin() {
        if (!enabled()) return;

        // Run "/adminv off" and hide the message
        if (autoAdminV()) {
            LegacyRecode.MC.player.chat("/adminv off");
        }

        // Run "/chat local" and hide the message
        if (autoChatLocal()) {
            LegacyRecode.MC.player.chat("/c l");
        }

        // Hide messages
        if (autoAdminV() ^ autoChatLocal()) {
            MessageGrabber.hide(1);
        } else if (autoAdminV() && autoChatLocal()) {
            MessageGrabber.hide(2);
        }
    }

    public static void handleStateChange(LegacyState oldState, LegacyState newState) {
        if (!enabled()) return;

        // If the state is changed to mode play, run "/chat local"
        // Note: May trigger simultaneously with StreamerHandler#handleServerJoin, but this is not a problem
        if (autoChatLocal() && newState.mode.equals(LegacyState.Mode.PLAY)) {
            LegacyRecode.MC.player.chat("/c l");
            MessageGrabber.hide(1);
        }
    }
}
