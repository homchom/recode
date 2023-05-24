@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.server.state

import io.github.homchom.recode.mc
import io.github.homchom.recode.server.*
import io.github.homchom.recode.ui.equalsUnstyled
import io.github.homchom.recode.ui.matchesUnstyled
import io.github.homchom.recode.util.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import net.minecraft.network.chat.Component

val ipMatchesDF get() = mc.currentServer?.ip?.matches(dfIPRegex) ?: false

private val dfIPRegex = Regex("""(?:\w+\.)?mcdiamondfire\.com(?::\d+)?""")

sealed interface DFState : LocateState {
    val permissions: Deferred<PermissionGroup>

    // TODO: implement this (how should we handle supportee vs support?)
    //val isInSession: Boolean

    /**
     * The player's current permissions, suspending if permissions have not yet been initially detected.
     */
    suspend fun permissions() = permissions.await()

    suspend fun withState(state: LocateState) = when (state) {
        is SpawnState -> AtSpawn(state.node, permissions, /*isInSession*/)
        is PlayState -> OnPlot(state, permissions(), /*isInSession*/)
    }

    fun withPermissions(permissions: PermissionGroup) = when (this) {
        is SpawnState -> AtSpawn(node, CompletableDeferred(permissions))
        is PlayState -> OnPlot(this, permissions)
    }

    class AtSpawn(
        override val node: Node,
        override val permissions: Deferred<PermissionGroup>,
        /*override val isInSession: Boolean*/
    ) : DFState, SpawnState {
        override suspend fun permissions() = permissions.await()
    }

    class OnPlot(
        state: PlayState,
        permissions: PermissionGroup,
        /*override val isInSession: Boolean*/
    ) : DFState, PlayState by state {
        override val permissions = CompletableDeferred(permissions)
    }
}

fun DFState?.isInMode(mode: PlotMode) = this is DFState.OnPlot && this.mode == mode

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
    val id = node.toIntOrNull()?.run { "node$node" } ?: node.uncapitalize()
    return Node(id)
}

//fun nodeOf(id: String) = Node.values().singleOrNull { it.id == id } ?: Node.UNKNOWN

data class Plot(
    val name: String,
    val owner: String,
    @get:JvmName("getId") val id: UInt
)

private val playModeRegex =
    Regex("""$GREEN_ARROW_CHAR Joined game: $PLOT_NAME_PATTERN by $USERNAME_PATTERN.""")

enum class PlotMode(val descriptor: String) : Matcher<Component, Unit> {
    Play("playing") {
        override fun match(input: Component) =
            playModeRegex.matchesUnstyled(input).unitOrNull()
    },
    Build("building") {
        override fun match(input: Component) =
            input.equalsUnstyled("$GREEN_ARROW_CHAR You are now in build mode.").unitOrNull()
    },
    Dev("coding") {
        override fun match(input: Component) =
            input.equalsUnstyled("$GREEN_ARROW_CHAR You are now in dev mode.").unitOrNull()
    };

    val id get() = name.lowercase()

    companion object : GroupMatcher<Component, Unit, PlotMode> by enumMatcher()
}

fun plotModeByDescriptor(descriptor: String) =
    PlotMode.values().single { it.descriptor == descriptor }
fun plotModeByDescriptorOrNull(descriptor: String) =
    PlotMode.values().singleOrNull { it.descriptor == descriptor }

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