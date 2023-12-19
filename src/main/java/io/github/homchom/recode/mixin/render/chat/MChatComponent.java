package io.github.homchom.recode.mixin.render.chat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.homchom.recode.feature.social.DGuiWithSideChat;
import io.github.homchom.recode.feature.social.MessageStacking;
import io.github.homchom.recode.feature.social.SideChat;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.ui.ChatUI;
import io.github.homchom.recode.ui.text.TextFunctions;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(ChatComponent.class)
public abstract class MChatComponent {
    @Shadow @Final private List<GuiMessage.Line> trimmedMessages;

    // side chat "overrides" for private members

    @ModifyVariable(method = "screenToChatX", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double overrideScreenToChatX(double screenX) {
        var sideChat = asSideChatOrNull();
        if (sideChat != null) screenX -= sideChat.getXOffset();
        return screenX;
    }

    // side chat redirects

    // TODO: improve handling of chat queue (partition it?)
    @WrapOperation(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/chat/ChatListener;queueSize()J"
    ))
    private long wrapMessageQueueSize(ChatListener listener, Operation<Long> operation) {
        return isSideChat() ? 0 : operation.call(listener);
    }

    // actions synced between main chat and side chat

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickSideChat(CallbackInfo ci) {
        if (isMainChat()) getSideChat().tick();
    }

    @Inject(method = "scrollChat", at = @At("HEAD"))
    private void scrollSideChat(int amount, CallbackInfo ci) {
        if (isMainChat()) getSideChat().scrollChat(amount);
    }

    @Inject(method = "clearMessages", at = @At("HEAD"))
    private void clearSideChat(boolean refresh, CallbackInfo ci) {
        if (isMainChat()) getSideChat().clearMessages(refresh);
    }

    // side chat helper methods

    @Unique
    private SideChat getSideChat() {
        var gui = (DGuiWithSideChat) Minecraft.getInstance().gui;
        return gui.getRecode$sideChat();
    }

    @Unique
    private boolean isMainChat() {
        var instance = (ChatComponent) (Object) this;
        return instance == Minecraft.getInstance().gui.getChat();
    }

    @Unique
    private boolean isSideChat() {
        var instance = (ChatComponent) (Object) this;
        return instance instanceof SideChat;
    }

    @Unique
    private @Nullable SideChat asSideChatOrNull() {
        var instance = (ChatComponent) (Object) this;
        return instance instanceof SideChat sideChat ? sideChat : null;
    }

    // message stacking

    @Unique
    private int trimmedMessageCount = 0;

    @Inject(
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V",
            at = @At("HEAD")
    )
    private void countTrimmedMessagesBeforeMessageStacking(CallbackInfo ci) {
        trimmedMessageCount = trimmedMessages.size();
    }

    @Inject(
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V",
            at = @At("TAIL")
    )
    private void stackMessages(
            Component message,
            MessageSignature signature,
            int tickDelta,
            GuiMessageTag tag,
            boolean refresh,
            CallbackInfo ci
    ) {
        if (!Config.getBoolean("stackDuplicateMsgs")) return;
        if (trimmedMessageCount == 0) return;

        // trimmedMessages[0] is the most recent message
        var lineCount = trimmedMessages.size() - trimmedMessageCount;
        if (trimmedMessageCount < lineCount) return;
        if (trimmedMessageCount > lineCount) {
            if (!trimmedMessages.get(lineCount * 2).endOfEntry()) return;
        }

        // return if messages aren't equal
        for (var index = lineCount; index < lineCount * 2; index++) {
            var firstLine = trimmedMessages.get(index);
            var secondLine = trimmedMessages.get(index - lineCount);
            if (!TextFunctions.looksLike(firstLine.content(), secondLine.content())) return;
            if (firstLine.endOfEntry() != secondLine.endOfEntry()) return;
        }

        // remove new message and update tags
        var oldTag = Objects.requireNonNull(trimmedMessages.get(lineCount).tag());
        var stackAmount = MessageStacking.getStackAmount(oldTag) + 1;

        trimmedMessages.subList(0, lineCount).clear();

        var newTag = ChatUI.plus(tag, MessageStacking.stackedMessageTag(stackAmount));
        for (var index = 0; index < lineCount; index++) {
            var oldLine = trimmedMessages.get(index);
            var newLine = new GuiMessage.Line(
                    oldLine.addedTime(),
                    oldLine.content(),
                    newTag,
                    oldLine.endOfEntry()
            );
            trimmedMessages.set(index, newLine);
        }
    }
}
