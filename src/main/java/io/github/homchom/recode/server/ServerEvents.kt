package io.github.homchom.recode.server

import io.github.homchom.recode.event.CustomEvent
import io.github.homchom.recode.event.HookEvent
import io.github.homchom.recode.event.ValidatedEvent
import io.github.homchom.recode.event.createEvent
import io.github.homchom.recode.sys.networking.LegacyState
import net.minecraft.network.chat.Component

object ReceiveChatMessageEvent :
    CustomEvent<Component, Boolean> by createEvent(),
    ValidatedEvent<Component>

object ChangeDFStateEvent :
    CustomEvent<StateChange, Unit> by createEvent(),
    HookEvent<StateChange>

data class StateChange(val new: LegacyState, val old: LegacyState)