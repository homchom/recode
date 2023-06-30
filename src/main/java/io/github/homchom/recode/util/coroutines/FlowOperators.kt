package io.github.homchom.recode.util.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

typealias StateFlowWithPrevious<T> = StateFlow<PairWithPrevious<T>>

/**
 * Returns a new [Flow] of [PairWithPrevious] objects.
 */
fun <T : Any> Flow<T>.pairWithPrevious() = flow {
    var old: T? = null
    collect { new ->
        emit(PairWithPrevious(old, new))
        old = new
    }
}

/**
 * A pair between a [new] value and [old], the previous value of its variable.
 */
data class PairWithPrevious<T : Any>(val old: T?, val new: T) {
    constructor(new: T) : this(null, new)
}