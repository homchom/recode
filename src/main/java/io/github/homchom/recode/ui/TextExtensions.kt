package io.github.homchom.recode.ui

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

operator fun MutableComponent.plusAssign(component: Component) {
    append(component)
}

infix fun Component.looksLike(other: Component) =
    toFlatList(Style.EMPTY) == other.toFlatList(Style.EMPTY)