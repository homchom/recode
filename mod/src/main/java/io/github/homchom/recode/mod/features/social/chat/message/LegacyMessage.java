package io.github.homchom.recode.mod.features.social.chat.message;

import io.github.homchom.recode.Logging;
import io.github.homchom.recode.mod.config.LegacyConfig;
import io.github.homchom.recode.mod.events.impl.LegacyReceiveSoundEvent;
import io.github.homchom.recode.sys.player.chat.MessageGrabber;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class LegacyMessage {
    private final Component text;
    private final CallbackInfo callback;
    private final LegacyMessageType type;

    private MessageCheck check;

    public LegacyMessage(ClientboundSystemChatPacket packet, CallbackInfo ci) {
        this.text = packet.content();
        this.callback = ci;
        this.type = MessageCheck.run(this);
        MessageFinalizer.run(this);
    }

    public Component getText() {
        return text;
    }

    public MessageCheck getCheck() {
        return check;
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
        callback.cancel();

        if (type.hasSound()) {
            LegacyReceiveSoundEvent.cancelNextSound();
        }
        MessageGrabber.hide(type.getMessageAmount() - 1);

        if (LegacyConfig.getBoolean("debugMode")) {
            Logging.logInfo("[CANCELLED] " + text.toString());
        }
    }
}
