package io.github.homchom.recode.server

import io.github.homchom.recode.util.capitalize
import io.github.homchom.recode.util.uncapitalize

sealed interface PlayState {
    val node: Node
    val plot: Plot
    val mode: PlotMode
    val status: String?
}

sealed interface LocateState {
    val username: String

    data class AtSpawn(override val username: String, val node: Node) : LocateState

    data class OnPlot(
        override val username: String,
        override val node: Node,
        override val plot: Plot,
        override val mode: PlotMode,
        override val status: String?
    ) : LocateState, PlayState
}

sealed interface DFState {
    val isInSession: Boolean

    data class AtSpawn(val node: String, override val isInSession: Boolean) : DFState

    data class OnPlot(
        override val node: Node,
        override val plot: Plot,
        override val mode: PlotMode,
        override val status: String?,
        override val isInSession: Boolean
    ) : DFState, PlayState {
        constructor(state: PlayState, isInSession: Boolean) :
                this(state.node, state.plot, state.mode, state.status, isInSession)
    }
}

@JvmInline
value class Node(private val id: String) {
    override fun toString() = when {
        id.startsWith("node") -> "Node ${id.drop(4)}"
        id == "beta" -> "Node Beta"
        else -> id.capitalize()
    }
}

fun nodeByName(name: String): Node {
    val node = name.removePrefix("Node ")
    val id = node.toIntOrNull()?.let { "node${node}" } ?: node.uncapitalize()
    return Node(id)
}

//fun nodeOf(id: String) = Node.values().singleOrNull { it.id == id } ?: Node.UNKNOWN

data class Plot(
    val name: String,
    val owner: String,
    val id: UInt
)

enum class PlotMode(val descriptor: String) {
    PLAY("playing"),
    BUILD("building"),
    DEV("coding");

    val id get() = name.lowercase()
}

fun plotModeByDescriptor(descriptor: String) =
    PlotMode.values().single { it.descriptor == descriptor }
fun plotModeByDescriptorOrNull(descriptor: String) =
    PlotMode.values().singleOrNull { it.descriptor == descriptor }