@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.multiplayer.state

import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.*
import io.github.homchom.recode.ui.equalsUnstyled
import io.github.homchom.recode.ui.matchesUnstyled
import io.github.homchom.recode.util.GroupMatcher
import io.github.homchom.recode.util.Matcher
import io.github.homchom.recode.util.enumMatcher
import io.github.homchom.recode.util.unitOrNull
import kotlinx.coroutines.Deferred
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.network.chat.Component

val ServerData?.ipMatchesDF get(): Boolean {
    val regex = Regex("""(?:\w+\.)?mcdiamondfire\.com(?::\d+)?""")
    return this?.ip?.matches(regex) ?: false
}

sealed interface DFState : LocateState {
    val permissions: Deferred<PermissionGroup>

    val session: SupportSession?

    /**
     * The player's current permissions, suspending if permissions have not yet been initially detected.
     */
    suspend fun permissions() = permissions.await()

    fun withState(state: LocateState) = when (state) {
        is SpawnState -> AtSpawn(state.node, permissions, session)
        is PlayState -> OnPlot(state, permissions, session)
    }

    fun withSession(session: SupportSession?): DFState

    data class AtSpawn(
        override val node: Node,
        override val permissions: Deferred<PermissionGroup>,
        override val session: SupportSession?
    ) : DFState, SpawnState {
        override fun withSession(session: SupportSession?) = copy(session = session)

        override fun equals(other: Any?) = super.equals(other)
        override fun hashCode() = super.hashCode()
    }

    data class OnPlot(
        override val node: Node,
        override val mode: PlotMode,
        override val plot: Plot,
        override val status: String?,
        override val permissions: Deferred<PermissionGroup>,
        override val session: SupportSession?
    ) : DFState, PlayState {
        constructor(
            state: PlayState,
            permissions: Deferred<PermissionGroup>,
            session: SupportSession?
        ) : this(state.node, state.mode, state.plot, state.status, permissions, session)

        override fun withSession(session: SupportSession?) = copy(session = session)

        override fun equals(other: Any?) = super.equals(other)
        override fun hashCode() = super.hashCode()
    }
}

fun DFState?.isOnPlot(plot: Plot) = this is DFState.OnPlot && this.plot == plot

fun DFState?.isInMode(mode: PlotMode) = this is DFState.OnPlot && this.mode == mode

@JvmInline
value class Node(private val id: String) {
    val displayName get() = when {
        id.startsWith("node") -> "Node ${id.drop(4)}"
        id == "beta" -> "Node Beta"
        else -> id.replaceFirstChar(Char::titlecase)
    }

    override fun toString() = displayName
}

fun nodeByName(name: String): Node {
    val node = name.removePrefix("Node ")
    val id = node.toIntOrNull()?.run { "node$node" } ?: node.replaceFirstChar(Char::lowercase)
    return Node(id)
}

data class Plot(
    val name: String,
    val owner: String,
    @get:JvmName("getId") val id: UInt
)

private val playModeRegex =
    Regex("""$MAIN_ARROW_CHAR Joined game: $PLOT_NAME_PATTERN by $USERNAME_PATTERN\.""")

enum class PlotMode(val descriptor: String) : Matcher<Component, Unit> {
    Play("playing") {
        override fun match(input: Component) =
            playModeRegex.matchesUnstyled(input).unitOrNull()
    },
    Build("building") {
        override fun match(input: Component) =
            input.equalsUnstyled("$MAIN_ARROW_CHAR You are now in build mode.").unitOrNull()
    },
    Dev("coding") {
        override fun match(input: Component) =
            input.equalsUnstyled("$MAIN_ARROW_CHAR You are now in dev mode.").unitOrNull()
    };

    val id get() = name.lowercase()

    val capitalizedDescriptor get() = descriptor.replaceFirstChar(Char::titlecase)

    companion object : GroupMatcher<Component, Unit, PlotMode> by enumMatcher()
}

fun plotModeByDescriptor(descriptor: String) =
    PlotMode.entries.single { it.descriptor == descriptor }
fun plotModeByDescriptorOrNull(descriptor: String) =
    PlotMode.entries.singleOrNull { it.descriptor == descriptor }

enum class SupportSession : Matcher<Component, Unit> {
    Requested {
        override fun match(input: Component) = input.equalsUnstyled(
            "You have requested code support.\nIf you wish to leave the queue, use /support cancel."
        ).unitOrNull()
    },
    Helping {
        override fun match(input: Component): Unit? {
            val regex = Regex("\\[SUPPORT] ${mc.player!!.username} entered a session with " +
                    "$USERNAME_PATTERN\\. $SUPPORT_ARROW_CHAR Queue cleared!")
            return regex.matchesUnstyled(input).unitOrNull()
        }
    };

    companion object : GroupMatcher<Component, Unit, SupportSession> by enumMatcher()
}

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

@Deprecated("Only used for legacy state", ReplaceWith("node.displayName"))
val LocateState.nodeDisplayName @JvmName("getNodeDisplayName") get() = node.displayName

sealed interface SpawnState : LocateState

sealed interface PlayState : LocateState {
    val plot: Plot
    val mode: PlotMode
    val status: String?
}