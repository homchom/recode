package io.github.homchom.recode.util.collections

/**
 * Adds and returns [element] to this collection.
 */
fun <T, S : T> MutableCollection<T>.with(element: S) = element.also(::add)

/**
 * Maps this list into an [Array].
 *
 * @see map
 */
inline fun <T, reified R> Collection<T>.mapToArray(transform: (T) -> R) =
    with(iterator()) {
        Array(size) { transform(next()) }
    }

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