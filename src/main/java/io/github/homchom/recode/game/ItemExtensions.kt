@file:JvmName("ItemExtensions")

package io.github.homchom.recode.game

import io.github.homchom.recode.mixin.game.ItemStackAccessor
import io.github.homchom.recode.ui.text.mergeStyleIfAbsent
import io.github.homchom.recode.ui.text.toAdventure
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack

/**
 * @return this [ItemStack]'s "lore" (description), found in its tooltip, as a list of [Component]s.
 */
fun ItemStack.lore(): List<Component> {
    val loreTag = tag
        ?.getCompoundOrNull("display")
        ?.getListOrNull("Lore", Tag.TAG_STRING)
        ?: return emptyList()

    return buildList(loreTag.size) {
        for (index in 0..<loreTag.size) {
            val text = JSONComponentSerializer.json().deserializeOrNull(loreTag.getString(index)) ?: continue
            val style = ItemStackAccessor.getLoreVanillaStyle().toAdventure()
            add(text.mergeStyleIfAbsent(style))
        }
    }
}