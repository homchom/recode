@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.server.state

import io.github.homchom.recode.server.*

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