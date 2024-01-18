package io.github.homchom.recode.util

import io.github.homchom.recode.util.Computation.Failure
import io.github.homchom.recode.util.Computation.Success

/**
 * A computed result that can either be a [Success] or [Failure], like the
 * [Either](https://docs.rs/either/latest/either/enum.Either.html) type found in many functional languages.
 */
sealed interface Computation<out S, out F> {
    fun successOrNull() = this as? Success<S>
    fun failureOrNull() = this as? Failure<F>

    class Success<out T>(override val value: T) : Computation<T, Nothing>, InvokableWrapper<T>
    class Failure<out T>(override val value: T) : Computation<Nothing, T>, InvokableWrapper<T>
}

inline fun <S, F, R> Computation<S, F>.map(transform: (S) -> R): Computation<R, F> =
    when (this) {
        is Success<S> -> Success(transform(value))
        is Failure<F> -> Failure(value)
    }