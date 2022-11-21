package io.github.homchom.recode.server

import io.github.homchom.recode.event.*
import io.github.homchom.recode.server.state.CurrentStateDetector
import io.github.homchom.recode.server.state.DFState
import net.minecraft.network.chat.Component

data class MessageContext(val message: Lazy<Message>, val raw: Component)

// TODO: replace Pair<..., Component> with ...
object ReceiveChatMessageEvent :
    CustomEvent<MessageContext, Boolean> by createEvent(),
    ValidatedEvent<MessageContext>

object ChangeDFStateEvent :
    CustomEvent<StateChange, Unit> by DependentEvent(createEvent(), CurrentStateDetector),
    HookEvent<StateChange>

data class StateChange(val new: DFState?, val old: DFState?)