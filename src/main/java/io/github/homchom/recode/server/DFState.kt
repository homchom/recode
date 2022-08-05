package io.github.homchom.recode.server

import net.minecraft.world.entity.player.Player

sealed interface DFState {
    val node: Node
    val inSession: Boolean

    data class AtSpawn(override val node: Node, override val inSession: Boolean) : DFState
    data class OnPlot(
        override val node: Node,
        val plot: Plot,
        val mode: PlotMode,
        override val inSession: Boolean
    ) : DFState
}

enum class Node(val id: String) {
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    BETA("beta"),
    DEV("dev"),
    DEV2("dev2"),
    UNKNOWN("?")
}

fun nodeOf(id: String) = Node.values().singleOrNull { it.id == id } ?: Node.UNKNOWN

data class Plot(
    val name: String,
    val owner: String,
    val id: UInt,
    val status: String
)

enum class PlotMode(val descriptor: String) {
    PLAY("playing"),
    BUILD("building"),
    DEV("coding");

    val id get() = name.lowercase()
}

fun plotModeOf(id: String) = PlotMode.values().single { it.id == id }