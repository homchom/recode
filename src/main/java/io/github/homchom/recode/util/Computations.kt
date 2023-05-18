package io.github.homchom.recode.util

import io.github.homchom.recode.util.Computation.Failure
import io.github.homchom.recode.util.Computation.Success

/**
 * Computes the nullable result of [block] with the ability to concisely short-circuit with [NullableScope.fail].
 *
 * **[NullableScope] should not be leaked.** See [FailScope] for more details.
 */
inline fun <T : Any> nullable(block: NullableScope.() -> T?): T? {
    val comp = computeIn(NullableScope, block)
    return (comp as? Computation.Success<T?>)?.value
}

/**
 * @see nullable
 */
sealed interface NullableScope : FailScope<Nothing?> {
    fun fail(): Nothing = fail(null)

    companion object Instance : NullableScope
}

/**
 * Computes the result of [block] with type [S], with the ability to concisely short-circuit with
 * [ComputeScope.fail].
 *
 * **[ComputeScope] should not be leaked.** See [FailScope] for more details.
 */
inline fun <S, F> compute(block: ComputeScope<F>.() -> S) = computeIn(ComputeScope(), block)

/**
 * @see compute
 */
@JvmInline
value class ComputeScope<T> private constructor(private val unit: NullableScope) : FailScope<T> {
    constructor() : this(NullableScope)
}

/**
 * Computes the result of [block] in [scope], where [scope] is a [FailScope].
 *
 * @see compute
 */
inline fun <S, F, C : FailScope<F>> computeIn(scope: C, block: C.() -> S): Computation<S, F> {
    return try {
        Computation.Success(scope.block())
    } catch (e: FailureException) {
        @Suppress("UNCHECKED_CAST")
        Computation.Failure(e.value as F)
    }
}

/**
 * A computed result that can either be a [Success] or [Failure], like the
 * [Either](https://docs.rs/either/latest/either/enum.Either.html) type found in many functional languages.
 */
interface Computation<out S, out F> {
    class Success<T>(val value: T) : Computation<T, Nothing>
    class Failure<T>(val value: T) : Computation<Nothing, T>
}

/**
 * A computation scope that can [fail], throwing a [FailureException]. Because such exceptions are designed to
 * be automatically caught by computation functions, derived types with supertype [FailScope] should **never** be
 * "leaked" into other contexts (e.g. by assigning it to a variable), unless it is to pass it to another scope that
 * adheres to this contract.
 *
 * @see computeIn
 */
sealed interface FailScope<T> {
    fun fail(value: T): Nothing = throw ComputationFailureException(value)
}

/**
 * An exception raised by failed computations, and subsequently caught by the functions they were started from.
 * Assuming no leakage, no manual exception handling should be necessary.
 */
sealed class FailureException(val value: Any?) : Exception()

private class ComputationFailureException(value: Any?) : FailureException(value)