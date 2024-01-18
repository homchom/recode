package io.github.homchom.recode.event

import io.github.homchom.recode.util.InvokableWrapper
import io.github.homchom.recode.util.math.MixedInt

/**
 * Creates a [SimpleValidated] event.
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
 * @property value
 * @property validity
 *
 * @see createValidatedEvent
 */
data class SimpleValidated<T> @JvmOverloads constructor(
    override val value: T,
    override var validity: MixedInt = MixedInt(1)
) : Validated, InvokableWrapper<T>

fun <T> Iterable<SimpleValidated<T>>.mapValid() = mapNotNull { if (it.isValid) it.value else null }