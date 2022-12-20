package io.github.homchom.recode.util

@OptIn(BreaksControlFlow::class)
inline fun <T : Any> nullable(block: NullableScope.() -> T?): T? {
    val comp = computeIn(NullableScopeInstance, block)
    return (comp as? Computation.Success<T?>)?.value
}

sealed interface NullableScope : FailScope<Nothing?> {
    fun fail(): Nothing = fail(null)
}
@BreaksControlFlow object NullableScopeInstance : NullableScope

@OptIn(BreaksControlFlow::class)
inline fun <S, F> compute(block: ComputeScope<F>.() -> S) = computeIn(ComputeScope(), block)

class ComputeScope<T> @BreaksControlFlow constructor() : FailScope<T>

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

sealed interface FailScope<T> {
    @OptIn(BreaksControlFlow::class)
    fun fail(value: T): Nothing = throw FailureException(value)
}

@BreaksControlFlow
class FailureException(val value: Any?) : Exception()

@RequiresOptIn("The following type uses unstructured control flow (via exceptions). " +
        "It is public for inline function use, but is essentially private and should not be " +
        "used directly")
annotation class BreaksControlFlow