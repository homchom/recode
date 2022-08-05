package io.github.homchom.recode.mod.features.social.chat.message;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.events.impl.LegacyReceiveSoundEvent;
import io.github.homchom.recode.sys.player.chat.MessageGrabber;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class LegacyMessage {

    private final ClientboundChatPacket packet;
    private final Component text;
    private final CallbackInfo callback;
    private final LegacyMessageType type;

    private MessageCheck check;
    private boolean cancelled;

    public LegacyMessage(ClientboundChatPacket packet, CallbackInfo ci) {
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

    public LegacyMessageType getType() {
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

    public boolean typeIs(LegacyMessageType toCompare) {
        return type == toCompare;
    }

    /**
     * Cancels this message. Also cancels the associated sound if there is any, plus cancels following messages if they are part.
     */
    public void cancel() {
        cancelled = true;

        callback.cancel();

        if (type.hasSound()) {
            LegacyReceiveSoundEvent.cancelNextSound();
        }
        MessageGrabber.hide(type.getMessageAmount() - 1);

        if (Config.getBoolean("debugMode")) {
            LegacyRecode.info("[CANCELLED] " + text.toString());
        }
    }
}
