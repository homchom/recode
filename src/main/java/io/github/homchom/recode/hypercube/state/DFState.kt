@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.hypercube.state

import io.github.homchom.recode.hypercube.MAIN_ARROW
import io.github.homchom.recode.hypercube.SUPPORT_ARROW
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.username
import io.github.homchom.recode.ui.text.equalsPlain
import io.github.homchom.recode.ui.text.matchEntirePlain
import io.github.homchom.recode.ui.text.matchesPlain
import io.github.homchom.recode.util.Matcher
import io.github.homchom.recode.util.matcherOf
import io.github.homchom.recode.util.regex.RegexModifier
import io.github.homchom.recode.util.regex.dynamicRegex
import io.github.homchom.recode.util.regex.regex
import kotlinx.coroutines.Deferred
import net.kyori.adventure.text.Component
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack

val ServerData?.ipMatchesDF get(): Boolean {
    val regex = regex {
        modify(RegexModifier.IgnoreCase)
        group {
            wordChar.oneOrMore()
            period
        }.optional()

        str("mcdiamondfire.com")

        group {
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
     * @return a new [DFState] derived from this one and [state], including calculated [PlotMode] state.
     */
    fun withState(state: LocateState) = when (state) {
        is LocateState.AtSpawn -> AtSpawn(state.node, permissions, session)
        is LocateState.OnPlot -> {
            val mode = when (state.mode) {
                PlotMode.Play -> PlotMode.Play

                PlotMode.Build -> PlotMode.Build

                PlotMode.Dev -> mc.player!!.let { player ->
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

    companion object {
        val EVENT = Node("event")
    }
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
    val plotName by any.oneOrMore() // plot name
    str(" by ")
    val plotOwner by any.oneOrMore() // not necessarily a username (i.e. Node.EVENT)
    period
}

sealed interface PlotMode {
    val id: ID

    sealed interface ID : Matcher<Component, MatchResult> {
        val descriptor: String

        val capitalizedDescriptor get() = descriptor.replaceFirstChar(Char::titlecase)

        companion object : Matcher<Component, MatchResult> {
            val entries get() = arrayOf(Play, Build, Dev)

            override fun match(input: Component) = matcherOf(*entries).match(input)
        }
    }

    data class MatchResult(val id: ID, val plotName: String?, val plotOwner: String?) {
        constructor(id: ID) : this(id, null, null)
    }

    data object Play : PlotMode, ID {
        override val id get() = this

        override val descriptor = "playing"

        override fun match(input: Component): MatchResult? {
            val regexMatch = playModeRegex.matchEntirePlain(input) ?: return null
            val plotName = regexMatch.groups["plotName"]?.value
            val plotOwner = regexMatch.groups["plotOwner"]?.value
            return MatchResult(this, plotName, plotOwner)
        }
    }

    data object Build : PlotMode, ID {
        override val id get() = this

        override val descriptor = "building"

        override fun match(input: Component): MatchResult? {
            val id = takeIf { input.equalsPlain("$MAIN_ARROW You are now in build mode.") }
            return id?.let(::MatchResult)
        }
    }

    data class Dev(val buildCorner: BlockPos, val referenceBookCopy: ItemStack) : PlotMode {
        override val id get() = ID

        companion object ID : PlotMode.ID {
            override val descriptor = "coding"

            override fun match(input: Component): MatchResult? {
                val id = takeIf { input.equalsPlain("$MAIN_ARROW You are now in dev mode.") }
                return id?.let(::MatchResult)
            }
        }
    }
}

enum class SupportSession : Matcher<Component, SupportSession> {
    Requested {
        override fun match(input: Component) = takeIf { input.equalsPlain(
            "You have requested code support.\nIf you wish to leave the queue, use /support cancel."
        ) }
    },
    Helping {
        private val regex = dynamicRegex { username: String ->
            str("[SUPPORT] $username entered a session with ")
            username()
            str(". $SUPPORT_ARROW Queue cleared!")
        }

        override fun match(input: Component): SupportSession? {
            val username = mc.player?.username ?: return null
            return takeIf { regex(username).matchesPlain(input) }
        }
    };

    companion object : Matcher<Component, SupportSession> by matcherOf(entries)
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