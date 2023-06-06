package io.github.homchom.recode.event

import io.github.homchom.recode.util.MixedInt

typealias SimpleValidatedEvent<T> = CustomEvent<SimpleValidated<T>, Boolean>
typealias SimpleValidatedListEvent<T> = CustomEvent<List<SimpleValidated<T>>, List<T>>

/**
 * Creates a [SimpleValidatedEvent].
 */
fun <T> createValidatedEvent() = createEvent<SimpleValidated<T>, Boolean> { it.isValid }

/**
 * A type that is validated by a [Listenable], mutating and returning [validity].
 */
interface Validated {
    /**
     * The validity of this context, where positive values represent "valid".
     */
    var validity: MixedInt

    val isValid get() = validity > 0

    fun validate(weight: MixedInt) {
        validity += weight
    }

    fun invalidate(weight: MixedInt) {
        validity -= weight
    }

    fun validate() = validate(MixedInt(1))
    fun invalidate() = invalidate(MixedInt(1))
}

/**
 * @see createValidatedEvent
 */
data class SimpleValidated<T> @JvmOverloads constructor(
    val value: T,
    override var validity: MixedInt = MixedInt(1)
) : Validated {
    operator fun invoke() = value
}

fun <T> Iterable<SimpleValidated<T>>.mapValid() = mapNotNull { if (it.isValid) it.value else null }

fun <T> Array<out SimpleValidated<T>>.mapValid() = mapNotNull { if (it.isValid) it.value else null }