package io.github.homchom.recode.util

/**
 * Runs [block] until it returns false. `callWhile(block)` is equivalent to `while(block())`
 * with an empty while block.
 */
inline fun callWhile(block: () -> Boolean) {
    do {
        val predicate = block()
    } while (predicate)
}