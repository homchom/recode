package io.github.homchom.recode

import io.github.homchom.recode.event.Requester
import io.github.homchom.recode.event.requester
import io.github.homchom.recode.event.trial
import io.github.homchom.recode.multiplayer.ReceiveChatMessageEvent
import io.github.homchom.recode.ui.equalsUnstyled

object NothingRequester : Requester<Unit, Unit> by requester(trial(
    ReceiveChatMessageEvent,
    start = { _ -> println("doing nothing :(") },
    tests = { _, (text), _ ->
        text.equalsUnstyled("pass pls").instantUnitOrNull()
    }
))