@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.multiplayer.state

import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.MAIN_ARROW
import io.github.homchom.recode.multiplayer.SUPPORT_ARROW
import io.github.homchom.recode.multiplayer.username
import io.github.homchom.recode.ui.equalsUnstyled
import io.github.homchom.recode.ui.matchesUnstyled
import io.github.homchom.recode.util.MatchPredicate
import io.github.homchom.recode.util.Matcher
import io.github.homchom.recode.util.matcher
import io.github.homchom.recode.util.regex.regex
import kotlinx.coroutines.Deferred
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

val ServerData?.ipMatchesDF get(): Boolean {
    val regex = regex {
        all {
            wordChar.oneOrMore()
            period
        }.optional()

        str("mcdiamondfire.com")

        all {
            str(":")
            digit.oneOrMore()
        }.optional()
    }

    return this?.ip?.matches(regex) ?: false
}

sealed interface DFState {
    val permissions: Deferred<PermissionGroup>

    val node: Node
    val session: SupportSession?

    /**
     * The player's current permissions, suspending if permissions have not yet been initially detected.
     */
    suspend fun permissions() = permissions.await()

    /**
     * Returns a new [DFState] derived from this one and [state], including calculated [PlotMode] state.
     */
    fun withState(state: LocateState) = when (state) {
        is LocateState.AtSpawn -> AtSpawn(state.node, permissions, session)
        is LocateState.OnPlot -> {
            val mode = when (state.mode) {
                PlotMode.Play -> PlotMode.Play

                PlotMode.Build -> PlotMode.Build

                PlotMode.Dev.ID -> mc.player!!.let { player ->
                    val buildCorner = player.blockPosition()
                        .mutable()
                        .setY(49)
                        .move(10, 0, -10)
                        .immutable()

                    val referenceBookCopy = player.inventory.getItem(17).copy()

                    PlotMode.Dev(buildCorner, referenceBookCopy)
                }
            }

            OnPlot(state.node, mode, state.plot, state.status, permissions, session)
        }
    }

    fun withSession(session: SupportSession?): DFState

    data class AtSpawn(
        override val node: Node,
        override val permissions: Deferred<PermissionGroup>,
        override val session: SupportSession?
    ) : DFState {
        override fun withSession(session: SupportSession?) = copy(session = session)

        override fun equals(other: Any?) = super.equals(other)
        override fun hashCode() = super.hashCode()
    }

    data class OnPlot(
        override val node: Node,
        val mode: PlotMode,
        val plot: Plot,
        val status: String?,
        override val permissions: Deferred<PermissionGroup>,
        override val session: SupportSession?
    ) : DFState {
        override fun withSession(session: SupportSession?) = copy(session = session)

        override fun equals(other: Any?) = super.equals(other)
        override fun hashCode() = super.hashCode()
    }
}

fun DFState?.isOnPlot(plot: Plot) = this is DFState.OnPlot && this.plot == plot

fun DFState?.isInMode(mode: PlotMode.ID) = this is DFState.OnPlot && this.mode.id == mode

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

private val playModeRegex = regex {
    str("$MAIN_ARROW Joined game: ")
    any.oneOrMore() // plot name
    str(" by ")
    username()
    period
}

sealed interface PlotMode {
    val id: ID

    sealed interface ID : MatchPredicate<Component> {
        val descriptor: String

        val capitalizedDescriptor get() = descriptor.replaceFirstChar(Char::titlecase)

        companion object : Matcher<Component, ID> {
            val entries get() = arrayOf(Play, Build, Dev)

            override fun match(input: Component) = matcher(*entries).match(input)
        }
    }

    data object Play : PlotMode, ID {
        override val id get() = this

        override val descriptor = "playing"

        override fun matches(input: Component) = playModeRegex.matchesUnstyled(input)
    }

    data object Build : PlotMode, ID {
        override val id get() = this

        override val descriptor = "building"

        override fun matches(input: Component) =
            input.equalsUnstyled("$MAIN_ARROW You are now in build mode.")
    }

    data class Dev(val buildCorner: BlockPos, val referenceBookCopy: ItemStack) : PlotMode {
        override val id get() = ID

        companion object ID : PlotMode.ID {
            override val descriptor = "coding"

            override fun matches(input: Component) =
                input.equalsUnstyled("$MAIN_ARROW You are now in dev mode.")
        }
    }
}

enum class SupportSession : MatchPredicate<Component> {
    Requested {
        override fun matches(input: Component) = input.equalsUnstyled(
            "You have requested code support.\nIf you wish to leave the queue, use /support cancel."
        )
    },
    Helping {
        override fun matches(input: Component): Boolean {
            val regex = regex {
                str("[SUPPORT] ${mc.player!!.username} entered a session with ")
                username()
                str(". $SUPPORT_ARROW Queue cleared!")
            }
            return regex.matchesUnstyled(input)
        }
    };

    companion object : Matcher<Component, SupportSession> by matcher(entries)
}

sealed interface LocateState {
    val node: Node

    data class AtSpawn(override val node: Node) : LocateState

    data class OnPlot(
        override val node: Node,
        val plot: Plot,
        val mode: PlotMode.ID,
        val status: String?
    ) : LocateState
}

@Deprecated("Only used for legacy state", ReplaceWith("node.displayName"))
val DFState.nodeDisplayName @JvmName("getNodeDisplayName") get() = node.displayName