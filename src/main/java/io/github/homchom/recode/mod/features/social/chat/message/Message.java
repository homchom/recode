package io.github.homchom.recode.mod.features.social.chat.message;

import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.events.impl.ReceiveSoundEvent;
import io.github.homchom.recode.sys.player.chat.MessageGrabber;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class Message {

    private final ClientboundChatPacket packet;
    private final Component text;
    private final CallbackInfo callback;
    private final MessageType type;

    private MessageCheck check;
    private boolean cancelled;

    public Message(ClientboundChatPacket packet, CallbackInfo ci) {
        this.packet = packet;
        this.text = packet.getMessage();
        this.callback = ci;
        this.type = MessageCheck.run(this);
        MessageFinalizer.run(this);
    }

    public ClientboundChatPacket getPacket() {
        return packet;
    }

    public Component getText() {
        return text;
    }

    public CallbackInfo getCallback() {
        return callback;
    }

    public MessageType getType() {
        return type;
    }

    public MessageCheck getCheck() {
        return check;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public String getStripped() {
        return text.getString();
    }

    public void setCheck(MessageCheck check) {
        this.check = check;
    }

    public boolean typeIs(MessageType toCompare) {
        return type == toCompare;
    }

    /**
     * Cancels this message. Also cancels the associated sound if there is any, plus cancels following messages if they are part.
     */
    public void cancel() {
        cancelled = true;

        callback.cancel();

        if (type.hasSound()) {
            ReceiveSoundEvent.cancelNextSound();
        }
        MessageGrabber.hide(type.getMessageAmount() - 1);

        if (Config.getBoolean("debugMode")) {
            Recode.info("[CANCELLED] " + text.toString());
        }
    }
}
