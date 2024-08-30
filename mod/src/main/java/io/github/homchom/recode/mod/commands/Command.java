package io.github.homchom.recode.mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class Command {
    public abstract void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context);

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

    protected void sendCommand(Minecraft mc, String message) {
        LocalPlayer player = mc.player;
        if (player != null) {
            player.connection.sendUnsignedCommand(message);
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
