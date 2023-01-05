package io.github.homchom.recode.util

// TODO: document about leakage
inline fun <T : Any> nullable(block: NullableScope.() -> T?): T? {
    val comp = computeIn(NullableScope, block)
    return (comp as? Computation.Success<T?>)?.value
}

sealed interface NullableScope : FailScope<Nothing?> {
    fun fail(): Nothing = fail(null)

    companion object Instance : NullableScope
}

inline fun <S, F> compute(block: ComputeScope<F>.() -> S) = computeIn(ComputeScope(), block)

class ComputeScope<T> : FailScope<T>

inline fun <S, F, R : FailScope<F>> computeIn(scope: R, block: R.() -> S): Computation<S, F> {
    return try {
        Computation.Success(scope.block())
    } catch (e: FailureException) {
        @Suppress("UNCHECKED_CAST")
        Computation.Failure(e.value as F)
    }
}

interface Computation<out S, out F> {
    class Success<T>(val value: T) : Computation<T, Nothing>
    class Failure<T>(val value: T) : Computation<Nothing, T>
}

sealed interface FailScope<T> {
    fun fail(value: T): Nothing = throw FailureException(value)
}

class FailureException(val value: Any?) : Exception()