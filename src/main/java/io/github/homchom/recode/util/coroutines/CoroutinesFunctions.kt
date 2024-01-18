package io.github.homchom.recode.util.coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Returns a [Job] with parent [parent] that is made active lazily.
 *
 * @see CoroutineStart.LAZY
 */
@DelicateCoroutinesApi
fun lazyJob(parent: Job? = null): Job {
    val context = parent ?: EmptyCoroutineContext
    return GlobalScope.launch(context, CoroutineStart.LAZY) { awaitCancellation() }
}