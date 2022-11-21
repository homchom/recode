package io.github.homchom.recode.util

@OptIn(BreaksControlFlow::class)
inline fun <T : Any> nullable(block: NullableScope.() -> T?): T? {
    val comp = computeIn(NullaryFailScope(null), block)
    return (comp as? Computation.Success<T?>)?.value
}

typealias NullableScope = NullaryFailScope<Nothing?>

// TODO: is this necessary? arguably not beneficial on the JVM with null safety
/*@OptIn(BreaksControlFlow::class)
inline fun <T> maybe(block: MaybeScope.() -> T): Maybe<T> {
    val comp = computeIn(NullaryFailScope(Maybe.No), block)
    return if (comp is Computation.Success<T>) Maybe.Yes(comp.value) else Maybe.No
}

typealias MaybeScope = NullaryFailScope<Maybe.No>

sealed interface Maybe<out T> {
    class Yes<T>(val value: T) : Maybe<T>
    object No : Maybe<Nothing>
}*/

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

class NullaryFailScope<T> @BreaksControlFlow constructor(private val failValue: T) : FailScope<T> {
    fun fail(): Nothing = fail(failValue)
}

@BreaksControlFlow
class FailureException(val value: Any?) : Exception()

@RequiresOptIn("The following type uses unstructured control flow (via exceptions). " +
        "It is public for inline function use, but is essentially private and should not be " +
        "used directly")
annotation class BreaksControlFlow