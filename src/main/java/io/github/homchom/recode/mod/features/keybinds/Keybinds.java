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

        // play
        KeyMapping play = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.play", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // build
        KeyMapping build = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.build", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // dev
        KeyMapping dev = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.dev", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // toggle play dev
        KeyMapping toggle_play_dev = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.toggle_play_dev", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // toggle play build
        KeyMapping toggle_play_build = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.toggle_play_build", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // spawn
        KeyMapping spawn = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.spawn", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // node1
        KeyMapping node1 = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.node1", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // node2
        KeyMapping node2 = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.node2", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // node3
        KeyMapping node3 = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.node3", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // node4
        KeyMapping node4 = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.node4", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // node5
        KeyMapping node5 = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.node5", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // node beta
        KeyMapping nodeBeta = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.node_beta", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // =======

        // fs normal
        KeyMapping fs_normal = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.fs_normal", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // fs med
        KeyMapping fs_med = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.fs_med", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // fs fast
        KeyMapping fs_fast = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.fs_fast", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // fs toggle normal med
        KeyMapping toggle_fs_normal_med = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.toggle_fs_normal_med", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // fs toggle normal fast
        KeyMapping toggle_fs_normal_fast = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.toggle_fs_normal_fast", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

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

        // chat global
        KeyMapping chatGlobal = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.chat_global", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // chat local
        KeyMapping chatLocal = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.chat_local", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // chat none
        KeyMapping chatNone = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.chat_none", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

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

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // play
            while (play.consumeClick()) {
                sendChat("/play");
            }

            // build
            while (build.consumeClick()) {
                sendChat("/build");
            }

            // dev
            while (dev.consumeClick()) {
                sendChat("/dev");
            }

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

            // node1
            while (node1.consumeClick()) {
                sendChat("/node 1");
            }

            // node2
            while (node2.consumeClick()) {
                sendChat("/node 2");
            }

            // node3
            while (node3.consumeClick()) {
                sendChat("/node 3");
            }

            // node4
            while (node4.consumeClick()) {
                sendChat("/node 4");
            }

            // node5
            while (node5.consumeClick()) {
                sendChat("/node 5");
            }

            // node beta
            while (nodeBeta.consumeClick()) {
                sendChat("/node beta");
            }

            // fs normal
            while (fs_normal.consumeClick()) {
                sendChat("/fs " + Config.getInteger("fsNormal"));
                FlightspeedToggle.fs_is_normal = true;
            }

            // fs med
            while (fs_med.consumeClick()) {
                sendChat("/fs " + Config.getInteger("fsMed"));
            }

            // fs fast
            while (fs_fast.consumeClick()) {
                sendChat("/fs " + Config.getInteger("fsFast"));
            }

            // toggle fs normal med
            while (toggle_fs_normal_med.consumeClick()) {
                FlightspeedToggle.toggleFlightspeed("medium");
            }

            // toggle fs normal fast
            while (toggle_fs_normal_fast.consumeClick()) {
                FlightspeedToggle.toggleFlightspeed("fast");
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

            // chat global
            while (chatGlobal.consumeClick()) {
                sendChat("/chat global");
            }

            // chat local
            while (chatLocal.consumeClick()) {
                sendChat("/chat local");
            }

            // chat none
            while (chatNone.consumeClick()) {
                sendChat("/chat none");
            }

            // search
            while (searchFunction.consumeClick()) {
                if (DFInfo.isOnDF() && DFInfo.currentState.getMode() == State.Mode.DEV && mc.player.isCreative()) {
                    BlockEntity blockEntity = mc.level.getBlockEntity(new BlockPos(mc.hitResult.getLocation()));

                    if (blockEntity != null) {
                        if (blockEntity instanceof SignBlockEntity) {
                            SignBlockEntity signBlockEntity = (SignBlockEntity) blockEntity;
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