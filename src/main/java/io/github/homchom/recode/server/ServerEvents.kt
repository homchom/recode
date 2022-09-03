package io.github.homchom.recode.server

import io.github.homchom.recode.event.*
import io.github.homchom.recode.sys.networking.LegacyState
import net.minecraft.network.chat.Component

object ReceiveChatMessageEvent :
    MatchedEvent<Component, Message, Boolean> by MatcherCallbackEvent(MessageMatcher),
    ValidatedEvent<Message>

object ChangeDFStateEvent :
    CustomEvent<StateChange, Unit> by createEvent(),
    HookEvent<StateChange>

data class StateChange(val new: LegacyState, val old: LegacyState)