package io.github.homchom.recode.util.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Constructs a [CoroutineScope] with context and a new [Job] derived from [parent].
 *
 * @param context Any additional context to combine.
 */
fun derivedCoroutineScope(parent: CoroutineScope, context: CoroutineContext = EmptyCoroutineContext) =
    CoroutineScope(parent.coroutineContext + Job(parent.coroutineContext.job) + context)

/**
 * Whether this [Job] has any children [Job]s.
 */
val Job.hasChildren get() = children.firstOrNull() != null