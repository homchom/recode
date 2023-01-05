package io.github.homchom.recode.util

@OptIn(BreaksControlFlow::class)
inline fun <T : Any> nullable(block: NullableScope.() -> T?): T? {
    val comp = computeIn(NullableScope, block)
    return (comp as? Computation.Success<T?>)?.value
}

@BreaksControlFlow
interface NullableScope : FailScope<Nothing?> {
    fun fail(): Nothing = fail(null)

    companion object Instance : NullableScope
}

@OptIn(BreaksControlFlow::class)
inline fun <S, F> compute(block: ComputeScope<F>.() -> S) = computeIn(ComputeScope(), block)

@BreaksControlFlow
class ComputeScope<T> : FailScope<T>

@OptIn(BreaksControlFlow::class)
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

@BreaksControlFlow
interface FailScope<T> {
    fun fail(value: T): Nothing = throw FailureException(value)
}

@BreaksControlFlow
class FailureException(val value: Any?) : Exception()

@RequiresOptIn("The following type uses unstructured control flow (via exceptions). " +
        "Be careful not to leak it so it is always wrapped inside a nullable {} call or try/catch")
annotation class BreaksControlFlow