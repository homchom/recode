package io.github.homchom.recode.mod.features.streamer;

import io.github.homchom.recode.hypercube.state.DFState;
import io.github.homchom.recode.hypercube.state.PlotMode;
import io.github.homchom.recode.mod.config.LegacyConfig;
import io.github.homchom.recode.sys.player.chat.MessageGrabber;
import net.minecraft.client.Minecraft;

public class StreamerModeHandler {

    public static boolean get(String key) {
        return LegacyConfig.getBoolean(key);
    }

    public static String getString(String key) {
        return LegacyConfig.getString(key);
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

    public static boolean hidePlotBoosts() {
        return getOption("streamerHidePlotBoosts");
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
            Minecraft.getInstance().player.connection.sendUnsignedCommand("adminv off");
        }

        // Run "/chat local" and hide the message
        if (autoChatLocal()) {
            Minecraft.getInstance().player.connection.sendUnsignedCommand("c l");
        }

        // Hide messages
        if (autoAdminV() ^ autoChatLocal()) {
            MessageGrabber.hide(1);
        } else if (autoAdminV() && autoChatLocal()) {
            MessageGrabber.hide(2);
        }
    }

    public static void handleStateChange(DFState newState) {
        if (!enabled()) return;

        // If the state is changed to mode play, run "/chat local"
        // Note: May trigger simultaneously with StreamerHandler#handleServerJoin, but this is not a problem
        if (autoChatLocal() && newState instanceof DFState.OnPlot playState &&
                playState.getMode().equals(PlotMode.Play.INSTANCE)) {
            Minecraft.getInstance().player.connection.sendUnsignedCommand("c l");
            MessageGrabber.hide(1);
        }
    }
}
