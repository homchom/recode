package io.github.homchom.recode.server

import io.github.homchom.recode.event.CustomListenable
import io.github.homchom.recode.event.Hook
import io.github.homchom.recode.event.ValidatedEvent
import io.github.homchom.recode.event.createEvent
import io.github.homchom.recode.sys.networking.LegacyState
import net.minecraft.network.chat.Component

object ReceiveChatMessageEvent :
    CustomListenable<Component, Boolean> by createEvent(),
    ValidatedEvent<Component>

object ChangeDFStateEvent :
    CustomListenable<StateChange, Unit> by createEvent(),
    Hook<StateChange>

data class StateChange(val new: LegacyState, val old: LegacyState)