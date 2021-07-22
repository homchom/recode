package io.github.codeutilities.sys.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.sys.util.chat.ChatType;
import io.github.codeutilities.sys.util.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public abstract class Command {
    public abstract void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd);

    protected boolean isCreative(MinecraftClient mc) {
        ClientPlayerEntity player = mc.player;
        if (player != null) {
            if (player.isCreative()) {
                return true;
            }
            ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
        }

        return false;
    }

    protected void sendMessage(MinecraftClient mc, Text message) {
        ClientPlayerEntity player = mc.player;
        this.sendMessage(player, message, false);
    }

    protected void sendChatMessage(MinecraftClient mc, String message) {
        ClientPlayerEntity player = mc.player;
        if (player != null) {
            player.sendChatMessage(message);
        }
    }

    protected void sendActionBar(MinecraftClient mc, Text message) {
        ClientPlayerEntity player = mc.player;
        this.sendMessage(player, message, true);
    }

    private void sendMessage(@Nullable ClientPlayerEntity playerEntity, Text message, boolean actionBar) {
        if (playerEntity != null) {
            playerEntity.sendMessage(message, actionBar);
        }
    }
}
