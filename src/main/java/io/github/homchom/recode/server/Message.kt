package io.github.homchom.recode.server

import io.github.homchom.recode.util.Matcher
import io.github.homchom.recode.util.MatcherList
import net.minecraft.network.chat.Component

sealed interface Message {
    object Chat : Message
}

private val matchers = MatcherList<Component, Message> { Message.Chat }
object MessageMatcher : Matcher<Component, Message> by matchers