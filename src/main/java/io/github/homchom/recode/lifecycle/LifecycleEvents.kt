package io.github.homchom.recode.lifecycle

import io.github.homchom.recode.event.CustomEvent
import io.github.homchom.recode.event.createEvent

object QuitGameEvent :
    CustomEvent<Unit, Unit> by createEvent()