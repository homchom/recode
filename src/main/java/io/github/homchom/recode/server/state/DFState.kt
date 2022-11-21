@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.server.state

import io.github.homchom.recode.mc
import io.github.homchom.recode.sys.networking.LegacyState
import io.github.homchom.recode.util.capitalize
import io.github.homchom.recode.util.uncapitalize

val isOnDF get() = mc.currentServer?.ip?.matches(dfIPRegex) ?: false

private val dfIPRegex = Regex("""(?:\w+\.)?mcdiamondfire\.com(?::\d+)?""")

sealed interface DFState : LocateState {
    val isInSession: Boolean

    fun withState(state: LocateState) = when (state) {
        is LocateState.AtSpawn, is AtSpawn -> AtSpawn(state.node, isInSession)
        is LocateState.OnPlot, is OnPlot -> OnPlot(state as PlayState, isInSession)
    }

    class AtSpawn(override val node: Node, override val isInSession: Boolean) : DFState, SpawnState
    class OnPlot(state: PlayState, override val isInSession: Boolean) : DFState, PlayState by state
}

@JvmInline
value class Node(private val id: String) {
    val displayName get() = when {
        id.startsWith("node") -> "Node ${id.drop(4)}"
        id == "beta" -> "Node Beta"
        else -> id.capitalize()
    }

    override fun toString() = displayName
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
    @get:JvmName("getId") val id: UInt
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

@Deprecated("Use DFState, not LegacyState")
fun LegacyState.toDFState(): DFState? {
    if (mode == null || mode == LegacyState.Mode.OFFLINE) return null
    val newNode = Node(node.raw)
    return if (mode == LegacyState.Mode.SPAWN) DFState.AtSpawn(newNode, session) else {
        val newPlot = Plot(plot.name, plot.owner, plot.id.toUInt())
        val newMode = plotModeByDescriptor(mode.continuousVerb.uncapitalize())
        val locateState = LocateState.OnPlot(newNode, newPlot, newMode, plot.status)
        DFState.OnPlot(locateState, session)
    }
}