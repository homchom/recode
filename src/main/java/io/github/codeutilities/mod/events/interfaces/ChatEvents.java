package io.github.codeutilities.mod.events.interfaces;

import io.github.codeutilities.mod.features.social.chat.message.Message;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface ChatEvents extends CustomEvent {
    Event<ChatEvents> RECEIVE_MESSAGE = EventFactory.createArrayBacked(ChatEvents.class,
            listeners -> message -> CustomEvent.makeEvent(listeners, listener -> ((ChatEvents) listener).receive(message)));

    ActionResult receive(Message message);
}
