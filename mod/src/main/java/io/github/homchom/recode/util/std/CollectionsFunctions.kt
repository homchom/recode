package io.github.homchom.recode.util.std

/**
 * Maps this list into an [Array].
 *
 * @see map
 */
inline fun <T, reified R> Collection<T>.mapToArray(transform: (T) -> R): Array<R> {
    val iterator = iterator()
    return Array(size) { transform(iterator.next()) }
}