package io.github.homchom.recode.mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.sys.player.chat.*;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class Command {
    public abstract void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd);

    protected boolean isCreative(Minecraft mc) {
        LocalPlayer player = mc.player;
        if (player != null) {
            if (player.isCreative()) {
                return true;
            }
            ChatUtil.sendTranslateMessage("recode.command.require_creative_mode", ChatType.FAIL);
        }

        return false;
    }

    protected void sendMessage(Minecraft mc, Component message) {
        LocalPlayer player = mc.player;
        this.sendMessage(player, message, false);
    }

    protected void sendChatMessage(Minecraft mc, String message) {
        LocalPlayer player = mc.player;
        if (player != null) {
            player.chat(message);
        }
    }

    protected void sendActionBar(Minecraft mc, Component message) {
        LocalPlayer player = mc.player;
        this.sendMessage(player, message, true);
    }

    private void sendMessage(@Nullable LocalPlayer playerEntity, Component message, boolean actionBar) {
        if (playerEntity != null) {
            playerEntity.displayClientMessage(message, actionBar);
        }
    }

    public abstract String getDescription();

    public abstract String getName();
}
