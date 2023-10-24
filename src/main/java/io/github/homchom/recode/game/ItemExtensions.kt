@file:JvmName("ItemExtensions")

package io.github.homchom.recode.game

import io.github.homchom.recode.mixin.game.ItemStackAccessor
import io.github.homchom.recode.ui.mergedWith
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

fun ItemStack.lore(): List<Component> {
    val loreTag = tag
        ?.getCompoundOrNull("display")
        ?.getListOrNull("Lore", Tag.TAG_STRING)
        ?: return emptyList()

    return buildList(loreTag.size) {
        for (index in 0..<loreTag.size) {
            // TODO: can this throw?
            val text = Component.Serializer.fromJson(loreTag.getString(index)) ?: continue
            add(text.mergedWith(ItemStackAccessor.getLoreStyle()))
        }
    }
}