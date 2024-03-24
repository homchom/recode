package io.github.homchom.recode.mod.features.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import io.github.homchom.recode.hypercube.state.DF;
import io.github.homchom.recode.hypercube.state.PlotMode;
import io.github.homchom.recode.mod.config.LegacyConfig;
import io.github.homchom.recode.mod.features.commands.CodeSearcher;
import io.github.homchom.recode.sys.sidedchat.ChatShortcut;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import java.util.Objects;
import java.util.Optional;

public class Keybinds implements ClientModInitializer {

    final Minecraft mc = Minecraft.getInstance();

    public static KeyMapping showTags;

    @Override
    public void onInitializeClient() {
        // =======================================================
        // Initialize
        // =======================================================
        // toggle play dev
        KeyMapping toggle_play_dev = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.toggle_play_dev", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // toggle play build
        KeyMapping toggle_play_build = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.toggle_play_build", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // spawn
        KeyMapping spawn = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.spawn", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // =======

        // fs toggle
        KeyMapping toggleFsMedium = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.toggle_fs_medium", InputConstants.Type.KEYSYM, -1, "key.category.recode"));
        KeyMapping toggleFsFast = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.toggle_fs_fast", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // =======

        // lagslayer
        KeyMapping lagslayer = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.lagslayer", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // rc
        KeyMapping rc = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.rc", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // rs
        KeyMapping rs = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.rs", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // plot spawn
        KeyMapping plotSpawn = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.plot_spawn", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // night vision
        KeyMapping nightvis = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.nightvis", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // fly
        KeyMapping fly = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.fly", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // search
        KeyMapping searchFunction = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.search", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // show tags

        showTags = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.recode.showTags", Type.KEYSYM, -1, "key.category.recode"
        ));

        // chat global
        KeyMapping chatGlobal = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.chat_global", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // chat local
        KeyMapping chatLocal = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.chat_local", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // chat none
        KeyMapping chatNone = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.chat_none", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // =======

        // Staff Keybinds
        KeyMapping modv = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.modv", InputConstants.Type.KEYSYM, -1, "key.category.recode"));
        KeyMapping supportAccept = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.support.accept", InputConstants.Type.KEYSYM, -1, "key.category.recode"));
        KeyMapping supportQueue = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.support.queue", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // register all the KeyMappings for the chat rooms
        for (ChatShortcut chatShortcut: ChatShortcut.values()) {
            ChatShortcut.addKeyMapping(KeyBindingHelper.registerKeyBinding(new KeyMapping(
                    chatShortcut.getTranslationKey(), -1, "key.category.recode"
            )), chatShortcut);
        }

        // =======================================================
        // Events
        // =======================================================

        // TODO: rework this with/after feature refactor
        FlightSpeedToggle fsToggle = new FlightSpeedToggle();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // toggle play dev
            while (toggle_play_dev.consumeClick()) {
                sendCommand(DF.isInMode(DF.getCurrentDFState(), PlotMode.Play.INSTANCE) ? "dev" : "play");
            }

            // toggle play build
            while (toggle_play_build.consumeClick()) {
                sendCommand(DF.isInMode(DF.getCurrentDFState(), PlotMode.Play.INSTANCE) ? "build" : "play");
            }

            // spawn
            while (spawn.consumeClick()) {
                sendCommand("s");
            }

            // toggle fs
            while (toggleFsMedium.consumeClick()) {
                fsToggle.toggleFlightSpeed(LegacyConfig.getInteger("fsMed"));
            }

            while (toggleFsFast.consumeClick()) {
                fsToggle.toggleFlightSpeed(LegacyConfig.getInteger("fsFast"));
            }

            // lagslayer
            while (lagslayer.consumeClick()) {
                sendCommand("lagslayer");
            }

            // rc
            while (rc.consumeClick()) {
                sendCommand("rc");
            }

            // rs
            while (rs.consumeClick()) {
                sendCommand("rs");
            }

            // plot spawn
            while (plotSpawn.consumeClick()) {
                sendCommand("p s");
            }

            // nightvis
            while (nightvis.consumeClick()) {
                sendCommand("nightvis");
            }

            // fly
            while (fly.consumeClick()) {
                sendCommand("fly");
            }

            // chat global
            while (chatGlobal.consumeClick()) {
                sendCommand("chat global");
            }

            // chat local
            while (chatLocal.consumeClick()) {
                sendCommand("chat local");
            }

            // chat none
            while (chatNone.consumeClick()) {
                sendCommand("chat none");
            }

            // search
            while (searchFunction.consumeClick()) {
                if (DF.isInMode(DF.getCurrentDFState(), PlotMode.Dev.ID)) {
                    var hitLocation = mc.hitResult.getLocation().toVector3f();
                    var blockPos = new BlockPos((int) hitLocation.x, (int) hitLocation.y, (int) hitLocation.z);
                    BlockEntity blockEntity = mc.level.getBlockEntity(blockPos);

                    if (blockEntity != null) {
                        if (blockEntity instanceof SignBlockEntity signBlockEntity) {
                            CodeSearcher.beginSearch(signBlockEntity);
                        } else {
                            CodeSearcher.clearSearch();
                        }
                    } else {
                        CodeSearcher.clearSearch();
                    }
                }
            }

            while (modv.consumeClick()) {
                sendCommand("mod v");
            }

            while (supportAccept.consumeClick()) {
                sendCommand("support accept");
            }

            while (supportQueue.consumeClick()) {
                sendCommand("support queue");
            }

            // chat shortcuts
            Optional<KeyMapping> pressedChatShortcut = ChatShortcut.KeyMappings().stream()
                    .filter(KeyMapping -> {
                        // filter which also needs to consume all of the wasPressed
                        // e.g. if multiple inputs went by before next frame was drawn
                        boolean pressed = false;
                        while (KeyMapping.consumeClick()) {
                            pressed = true;
                        }
                        return pressed;
                    })
                    // will only handle the first, if for some reason you bind multiple chats to one button
                    .findFirst();

            // if any chat shortcut was pressed
            if (pressedChatShortcut.isPresent()) {
                ChatShortcut chatShortcut = ChatShortcut.getFromKey(pressedChatShortcut.get());

                ChatShortcut.setCurrentChatShortcut(chatShortcut);
                mc.setScreen(new ChatScreen(""));
            }
        });
    }

    private void sendCommand(String message) {
        Objects.requireNonNull(mc.player).connection.sendUnsignedCommand(message);
    }
}