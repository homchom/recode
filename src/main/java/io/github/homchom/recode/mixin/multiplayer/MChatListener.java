package io.github.homchom.recode.mixin.multiplayer;

import io.github.homchom.recode.feature.social.MCGuiWithSideChat;
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
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatListener.class)
public class MChatListener {
    @Redirect(method = "showMessageToPlayer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V"
    ))
    private void partitionSignedMessages(
            ChatComponent chat,
            Component message,
            MessageSignature signature,
            GuiMessageTag tag
    ) {
        if (matchToChatSide(message) == ChatRule.ChatSide.SIDE) {
            getSideChat().addMessage(message, signature, tag);
        } else {
            chat.addMessage(message, signature, tag);
        }
    }

    @Redirect(method = { "handleSystemMessage", "method_45745" }, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;)V"
    ))
    private void partitionUnsignedMessages(ChatComponent chat, Component message) {
        if (matchToChatSide(message) == ChatRule.ChatSide.SIDE) {
            getSideChat().addMessage(message);
        } else {
            chat.addMessage(message);
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
        var gui = (MCGuiWithSideChat) Minecraft.getInstance().gui;
        return gui.recode$getSideChat();
    }
}
