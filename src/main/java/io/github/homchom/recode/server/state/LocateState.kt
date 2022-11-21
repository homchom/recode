@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.server.state

import io.github.homchom.recode.server.Message
import io.github.homchom.recode.server.ReceiveChatMessageEvent
import io.github.homchom.recode.server.defineRequest
import io.github.homchom.recode.server.sendCommand
import io.github.homchom.recode.util.invoke

val LocateRequest by defineRequest(
    ReceiveChatMessageEvent,
    executor = { username: String? -> sendCommand(if (username == null) "locate" else "locate $username") },
    test = { it.message() as? Message.Locate }
)

sealed interface LocateState {
    val node: Node

    data class AtSpawn(override val node: Node) : SpawnState

    data class OnPlot(
        override val node: Node,
        override val plot: Plot,
        override val mode: PlotMode,
        override val status: String?
    ) : PlayState
}

@Deprecated("Only used for legacyState", ReplaceWith("node.displayName"))
val LocateState.nodeDisplayName @JvmName("getNodeDisplayName") get() = node.displayName

sealed interface SpawnState : LocateState

sealed interface PlayState : LocateState {
    val plot: Plot
    val mode: PlotMode
    val status: String?
}