package io.github.homchom.recode.server

import io.github.homchom.recode.util.unmodifiable

/**
 * A group of server command aliases. Add here as needed.
 */
// TODO: does this suck? remove if so
enum class CommandAliasGroup(vararg aliases: String) {
    RENAME("rename", "i name", "item name"),
    ITEM_LORE_ADD("addlore", "ila", "lore add", "i lore add", "item lore add"),
    ITEM_LORE_SET("ils", "sll", "lore set", "i lore set", "item lore set", "setloreline"),
    PLOT_NAME("p name", "plot name");

    val aliases = aliases.asList().unmodifiable()

    operator fun unaryPlus() = aliases
}