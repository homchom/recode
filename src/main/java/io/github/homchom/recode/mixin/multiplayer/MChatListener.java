package io.github.homchom.recode.mixin.multiplayer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.homchom.recode.feature.social.DGuiWithSideChat;
import io.github.homchom.recode.feature.social.SideChat;
import io.github.homchom.recode.sys.sidedchat.ChatRule;
import io.github.homchom.recode.sys.util.SoundUtil;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatListener.class)
public abstract class MChatListener {
    @WrapOperation(method = "showMessageToPlayer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V"
    ))
    private void partitionSignedMessages(
            ChatComponent mainChat,
            Component message,
            MessageSignature signature,
            GuiMessageTag tag,
            Operation<Void> operation
    ) {
        if (matchToChatSide(message) == ChatRule.ChatSide.SIDE) {
            getSideChat().addMessage(message, signature, tag);
        } else {
            operation.call(mainChat, message, signature, tag);
        }
    }

    @WrapOperation(method = {"handleSystemMessage", "method_45745"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;)V"
    ))
    private void partitionUnsignedMessages(ChatComponent mainChat, Component message, Operation<Void> operation) {
        if (matchToChatSide(message) == ChatRule.ChatSide.SIDE) {
            getSideChat().addMessage(message);
        } else {
            operation.call(mainChat, message);
        }
    }

    @Unique
    private ChatRule.ChatSide matchToChatSide(Component message) {
        var side = ChatRule.ChatSide.MAIN;
        for (var chatRule : ChatRule.getChatRules()) {
            if (!chatRule.matches(message)) continue;

            if (chatRule.getChatSide() != ChatRule.ChatSide.EITHER) {
                side = chatRule.getChatSide();
            }

            if (chatRule.getChatSound() != null) {
                SoundUtil.playSound(chatRule.getChatSound());
            }
        }
        return side;
    }

    @Unique
    private SideChat getSideChat() {
        var gui = (DGuiWithSideChat) Minecraft.getInstance().gui;
        return gui.getRecode$sideChat();
    }
}
