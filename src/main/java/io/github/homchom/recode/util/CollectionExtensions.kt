package io.github.homchom.recode.util

import java.util.Collections

fun <T> List<T>.unmodifiable(): List<T> = Collections.unmodifiableList(this)
fun <K, V> Map<out K, V>.unmodifiable(): Map<K, V> = Collections.unmodifiableMap(this)
fun <T> Set<T>.unmodifiable(): Set<T> = Collections.unmodifiableSet(this)
fun <T> Collection<T>.unmodifiable(): Collection<T> = Collections.unmodifiableCollection(this)

/**
 * Flattens an [Iterable] of [Iterable]s vertically. For each index n starting at 0, the nth element of each
 * collection is added if it exists, and a new [List] is returned.
 */
fun <T> Iterable<Iterable<T>>.verticalFlatten() = map { it.iterator() }.let { outer ->
    buildList {
        do {
            val columnNotEmpty = outer.any {
                val hasNext = it.hasNext()
                if (hasNext) add(it.next())
                hasNext
            }
        } while (columnNotEmpty)
    }
}