package io.github.codeutilities.keybinds;

import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.util.DFInfo;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class Keybinds implements ClientModInitializer {

    MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {

        // =======================================================
        // Initialize
        // =======================================================

        // play
        KeyBinding play = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.play", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // build
        KeyBinding build = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.build", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // dev
        KeyBinding dev = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.dev", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // toggle play dev
        KeyBinding toggle_play_dev = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.toggle_play_dev", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // toggle play build
        KeyBinding toggle_play_build = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.toggle_play_build", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // spawn
        KeyBinding spawn = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.spawn", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // node1
        KeyBinding node1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.node1", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // node2
        KeyBinding node2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.node2", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // node3
        KeyBinding node3 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.node3", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // node4
        KeyBinding node4 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.node4", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // node5
        KeyBinding node5 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.node5", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // node beta
        KeyBinding nodeBeta = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.node_beta", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // =======

        // fs normal
        KeyBinding fs_normal = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.fs_normal", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // fs med
        KeyBinding fs_med = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.fs_med", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // fs fast
        KeyBinding fs_fast = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.fs_fast", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // fs toggle normal med
        KeyBinding toggle_fs_normal_med = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.toggle_fs_normal_med", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // fs toggle normal fast
        KeyBinding toggle_fs_normal_fast = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.toggle_fs_normal_fast", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // =======

        // lagslayer
        KeyBinding lagslayer = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.lagslayer", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // rc
        KeyBinding rc = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.rc", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // rs
        KeyBinding rs = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.rs", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // plot spawn
        KeyBinding plotSpawn = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.plot_spawn", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // night vision
        KeyBinding nightvis = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.nightvis", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // fly
        KeyBinding fly = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.fly", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // =======

        // chat global
        KeyBinding chatGlobal = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.chat_global", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // chat local
        KeyBinding chatLocal = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.chat_local", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // chat none
        KeyBinding chatNone = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.codeutilities.chat_none", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

        // =======================================================
        // Events
        // =======================================================

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // play
            while (play.wasPressed()) {
                sendChat("/play");
            }

            // build
            while (build.wasPressed()) {
                sendChat("/build");
            }

            // dev
            while (dev.wasPressed()) {
                sendChat("/dev");
            }

            // toggle play dev
            while (toggle_play_dev.wasPressed()) {
                sendChat(DFInfo.currentState == DFInfo.State.PLAY ? "/dev" : "/play");
            }

            // toggle play build
            while (toggle_play_build.wasPressed()) {
                sendChat(DFInfo.currentState == DFInfo.State.PLAY ? "/build" : "/play");
            }

            // spawn
            while (spawn.wasPressed()) {
                sendChat("/s");
            }

            // node1
            while (node1.wasPressed()) {
                sendChat("/node 1");
            }

            // node2
            while (node2.wasPressed()) {
                sendChat("/node 2");
            }

            // node3
            while (node3.wasPressed()) {
                sendChat("/node 3");
            }

            // node4
            while (node4.wasPressed()) {
                sendChat("/node 4");
            }

            // node5
            while (node5.wasPressed()) {
                sendChat("/node 5");
            }

            // node beta
            while (nodeBeta.wasPressed()) {
                sendChat("/node beta");
            }

            // fs normal
            while (fs_normal.wasPressed()) {
                sendChat("/fs " + ModConfig.getConfig().fsNormal);
                FlightspeedToggle.fs_is_normal = true;
            }

            // fs med
            while (fs_med.wasPressed()) {
                sendChat("/fs " + ModConfig.getConfig().fsMed);
            }

            // fs fast
            while (fs_fast.wasPressed()) {
                sendChat("/fs " + ModConfig.getConfig().fsFast);
            }

            // toggle fs normal med
            while (toggle_fs_normal_med.wasPressed()) {
                FlightspeedToggle.toggleFlightspeed("medium");
            }

            // toggle fs normal fast
            while (toggle_fs_normal_fast.wasPressed()) {
                FlightspeedToggle.toggleFlightspeed("fast");
            }

            // lagslayer
            while (lagslayer.wasPressed()) {
                sendChat("/lagslayer");
            }

            // rc
            while (rc.wasPressed()) {
                sendChat("/rc");
            }

            // rs
            while (rs.wasPressed()) {
                sendChat("/rs");
            }

            // plot spawn
            while (plotSpawn.wasPressed()) {
                sendChat("/p s");
            }

            // nightvis
            while (nightvis.wasPressed()) {
                sendChat("/nightvis");
            }

            // fly
            while (fly.wasPressed()) {
                sendChat("/fly");
            }

            // chat global
            while (chatGlobal.wasPressed()) {
                sendChat("/c 2");
            }

            // chat local
            while (chatLocal.wasPressed()) {
                sendChat("/c 1");
            }

            // chat none
            while (chatNone.wasPressed()) {
                sendChat("/c 0");
            }

        });
    }

    private void sendChat(String message) {
        assert mc.player != null;
        mc.player.sendChatMessage(message);
    }
}