package io.github.homchom.recode.mod.features;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import io.github.homchom.recode.mod.commands.impl.other.PartnerBracketCommand;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.commands.CodeSearcher;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.sidedchat.ChatShortcut;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.*;

import java.util.*;

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

        // spawn
        KeyMapping spawn = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.spawn", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // =======

        // fs normal
        KeyMapping fsNormal = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.fs_normal", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // fs med
        KeyMapping fsMedium = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.fs_med", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

        // fs fast
        KeyMapping fsFast = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.recode.fs_fast", InputConstants.Type.KEYSYM, -1, "key.category.recode"));

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

        // Sided Chat

        // =======================================================
        // Events
        // =======================================================

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // play
            while (play.consumeClick()) {
                sendCommand("play");
            }

            // build
            while (build.consumeClick()) {
                sendCommand("build");
            }

            // dev
            while (dev.consumeClick()) {
                sendCommand("dev");
            }

            // spawn
            while (spawn.consumeClick()) {
                sendCommand("s");
            }

            // fs normal
            while (fsNormal.consumeClick()) {
                sendCommand("fs " + Config.getInteger("fsNormal"));
            }

            // fs med
            while (fsMedium.consumeClick()) {
                sendCommand("fs " + Config.getInteger("fsMed"));
            }

            // fs fast
            while (fsFast.consumeClick()) {
                sendCommand("fs " + Config.getInteger("fsFast"));
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
                if (DFInfo.isOnDF() && DFInfo.currentState.getMode() == LegacyState.Mode.DEV && mc.player.isCreative()) {
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
        Objects.requireNonNull(mc.player).commandUnsigned(message);
    }
}