package io.github.homchom.recode.mod.features.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import io.github.homchom.recode.mod.commands.impl.other.PartnerBracketCommand;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.commands.CodeSearcher;
import io.github.homchom.recode.sys.networking.State;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.sidedchat.ChatShortcut;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.*;

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

        KeyMapping partnerBracket = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.recode.partnerBracket", Type.KEYSYM, -1, "key.category.recode"
        ));

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

//        // Sided Chat
//        Map<ChatType, KeyMapping> chatTypeKeyMappingMap = new HashMap<>();
//        for (ChatRule.ChatRuleType chatType :
//                ChatType.values()) {
//            chatTypeKeyMappingMap.put(Chat)
//        }

        // =======================================================
        // Events
        // =======================================================

        // TODO: rework this with/after feature refactor
        FlightSpeedToggle fsToggle = new FlightSpeedToggle();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // toggle play dev
            while (toggle_play_dev.consumeClick()) {
                sendChat(DFInfo.currentState.getMode() == State.Mode.PLAY ? "/dev" : "/play");
            }

            // toggle play build
            while (toggle_play_build.consumeClick()) {
                sendChat(DFInfo.currentState.getMode() == State.Mode.PLAY ? "/build" : "/play");
            }

            // spawn
            while (spawn.consumeClick()) {
                sendChat("/s");
            }

            // toggle fs
            while (toggleFsMedium.consumeClick()) {
                fsToggle.toggleFlightSpeed(Config.getInteger("fsMed"));
            }

            while (toggleFsFast.consumeClick()) {
                fsToggle.toggleFlightSpeed(Config.getInteger("fsFast"));
            }

            // lagslayer
            while (lagslayer.consumeClick()) {
                sendChat("/lagslayer");
            }

            // rc
            while (rc.consumeClick()) {
                sendChat("/rc");
            }

            // rs
            while (rs.consumeClick()) {
                sendChat("/rs");
            }

            // plot spawn
            while (plotSpawn.consumeClick()) {
                sendChat("/p s");
            }

            // nightvis
            while (nightvis.consumeClick()) {
                sendChat("/nightvis");
            }

            // fly
            while (fly.consumeClick()) {
                sendChat("/fly");
            }

            // search
            while (searchFunction.consumeClick()) {
                if (DFInfo.isOnDF() && DFInfo.currentState.getMode() == State.Mode.DEV && mc.player.isCreative()) {
                    BlockEntity blockEntity = mc.level.getBlockEntity(new BlockPos(mc.hitResult.getLocation()));

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

            while (partnerBracket.consumeClick()) {
                PartnerBracketCommand.exec();
            }

            while (modv.consumeClick()) {
                sendChat("/mod v");
            }

            while (supportAccept.consumeClick()) {
                sendChat("/support accept");
            }

            while (supportQueue.consumeClick()) {
                sendChat("/support queue");
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

    private void sendChat(String message) {
        assert mc.player != null;
        mc.player.chat(message);
    }
}