package io.github.homchom.recode.hypercube

/**
 * A group of server command aliases. Add here as needed.
 */
enum class CommandAliasGroup(vararg aliases: String) : Set<String> by aliases.toSet() {
    // value items
    NUMBER("number", "num"),
    STRING("string", "str"),
    TEXT("text", "txt", "styledtext", "stxt"),
    VARIABLE("variable", "var"),

    // renaming
    ITEM_NAME("item name", "i name", "rename"),
    ITEM_LORE_ADD("item lore add", "i lore add", "i l add", "ila", "lore add", "addlore"),
    ITEM_LORE_SET("item lore set", "i lore set", "i l set", "ils", "lore set", "setlore"),
    PLOT_NAME("plot name", "p name"),

    // legacy
    RELORE("relore");
}