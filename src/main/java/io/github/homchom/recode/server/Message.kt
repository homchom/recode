package io.github.homchom.recode.server

import io.github.homchom.recode.util.TrialMatcherList
import net.minecraft.network.chat.Component

sealed interface Message {
    object Chat : Message
}

object MessageMatcher : TrialMatcherList<Component, Message>() {
    override fun default(input: Component) = Message.Chat
}