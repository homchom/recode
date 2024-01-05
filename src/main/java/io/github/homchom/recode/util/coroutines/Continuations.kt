package io.github.homchom.recode.util.coroutines

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor

/**
 * Returns a new [ContinuationInterceptor] that invokes [onResume] before each successful resumption.
 */
// we don't optimize with Delay because CancellableContinuation is very internal
fun ContinuationInterceptor.withNotifications(onResume: () -> Unit): ContinuationInterceptor =
    object : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
        override fun <T> interceptContinuation(continuation: Continuation<T>) =
            Continuation(continuation.context) { result ->
                if (result.isSuccess) onResume()
                val intercepted = this@withNotifications.interceptContinuation(continuation)
                intercepted.resumeWith(result)
            }
    }