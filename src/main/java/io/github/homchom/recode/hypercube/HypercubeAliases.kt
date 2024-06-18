package io.github.homchom.recode.hypercube

import io.github.homchom.recode.util.regex.regex

/**
 * A container for [Regex] wrappers that matches Hypercube command literals and their aliases, including the
 * trailing space. Add to this object as needed.
 */
object HypercubeCommandAliases {
    // item manipulation
    private val ITEM = Word("item", "i")
    private val LORE = Word("lore", "l")

    val ITEM_NAME = command(
        arrayOf(ITEM, Word("name", "n")),
        arrayOf(Word("rename"))
    )
    val ITEM_LORE_ADD = command(
        arrayOf(ITEM, LORE, Word("add", "a")),
        arrayOf(LORE, Word("add", "a")),
        arrayOf(Word("addlore", "ila")),
    )
    val ITEM_LORE_SET = command(
        arrayOf(ITEM, LORE, Word("set", "s")),
        arrayOf(LORE, Word("set", "s")),
        arrayOf(Word("setlore", "ils"))
    )
    val ITEM_LORE_INSERT = command(
        arrayOf(ITEM, LORE, Word("insert", "i")),
        arrayOf(LORE, Word("insert", "i"))
    )

    // plot settings
    val PLOT_NAME = command(Word("plot", "p"), Word("name"))

    // value items
    val NUMBER = command("number", "num")
    val STRING = command("string", "str")
    val TEXT = command("styledtext", "text", "stxt", "txt")
    val VARIABLE = command("variable", "var")

    // legacy
    val RELORE = command("relore")

    private fun command(vararg aliases: Array<out Word>) = regex {
        for (index in aliases.indices) {
            group {
                for (word in aliases[index]) {
                    anyStr(*word.aliases)
                    space
                }
            }
            if (index != aliases.lastIndex) or
        }
    }

    private fun command(vararg words: Word) = command(arrayOf(*words))
    private fun command(long: String, vararg short: String) = command(Word(long, *short))

    private class Word(long: String, vararg short: String) {
        val aliases = arrayOf(long, *short)
    }
}