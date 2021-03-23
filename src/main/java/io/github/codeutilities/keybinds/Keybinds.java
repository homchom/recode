package io.github.codeutilities.keybinds;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.StickyKeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

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

            // fs 100
            KeyBinding fs100 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.codeutilities.fs100", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

            // fs 360
            KeyBinding fs360 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.codeutilities.fs360", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

            // fs 1000
            KeyBinding fs1000 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.codeutilities.fs1000", InputUtil.Type.KEYSYM, -1, "key.category.codeutilities"));

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

                // fs 100
                while (fs100.wasPressed()) {
                    sendChat("/fs 100");
                }

                // fs 360
                while (fs360.wasPressed()) {
                    sendChat("/fs 360");
                }

                // fs 1000
                while (fs1000.wasPressed()) {
                    sendChat("/fs 1000");
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

            });
        }

        private void sendChat(String message) {
            assert mc.player != null;
            mc.player.sendChatMessage(message);
        }
    }