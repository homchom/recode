package io.github.homchom.recode.util.collections

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