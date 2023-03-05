package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.lifecycle.RModule

interface Detector<T : Any, R : Any> : Listenable<R> {
    suspend fun detectFrom(module: RModule, input: T?, basis: Listenable<*>? = null): R?

    suspend fun checkNextFrom(module: RModule, input: T?, basis: Listenable<*>? = null, attempts: UInt = 1u): R?
}

interface Requester<T : Any, R : Any> : Detector<T, R> {
    suspend fun requestFrom(module: RModule, input: T): R

    suspend fun requestNextFrom(module: RModule, input: T, attempts: UInt = 1u): R
}

interface DetectorModule<T : Any, R : Any> : Detector<T, R>, RModule

interface RequesterModule<T : Any, R : Any> : Requester<T, R>, RModule

sealed interface Trial<S> {
    val basis: Listenable<*>

    fun supplyResultsFrom(module: ExposedModule): Listenable<S>
}

sealed interface DetectorTrial<T : Any, R : Any> : Trial<DetectorTrial.ResultSupplier<T, R>> {
    fun interface Tester<T : Any, B, R : Any> {
        suspend fun TrialScope.runTests(input: T?, baseContext: B): R?

        suspend fun runTestsIn(scope: TrialScope, input: T?, baseContext: B) = scope.runTests(input, baseContext)
    }

    fun interface NullaryTester<B, R : Any> {
        suspend fun TrialScope.runTests(baseContext: B): R?

        suspend fun runTestsIn(scope: TrialScope, baseContext: B) = scope.runTests(baseContext)

        @Suppress("RemoveRedundantQualifierName")
        fun toUnary() = DetectorTrial.Tester<Unit, B, R> { _, baseContext -> runTests(baseContext) }
    }

    fun interface ResultSupplier<T : Any, R : Any> {
        suspend fun TrialScope.supply(input: T?): R?

        suspend fun supplyIn(scope: TrialScope, input: T?) = scope.supply(input)
    }
}

sealed interface RequesterTrial<T : Any, R : Any> : Trial<RequesterTrial.ResultSupplier<T, R>> {
    val start: suspend (input: T) -> R?

    fun interface Tester<T : Any, B, R : Any> {
        suspend fun TrialScope.runTests(input: T?, baseContext: B, isRequest: Boolean): R?

        suspend fun runTestsIn(scope: TrialScope, input: T?, baseContext: B, isRequest: Boolean) =
            scope.runTests(input, baseContext, isRequest)
    }

    fun interface NullaryTester<B, R : Any> {
        suspend fun TrialScope.runTests(baseContext: B, isRequest: Boolean): R?

        suspend fun runTestsIn(scope: TrialScope, baseContext: B, isRequest: Boolean) =
            scope.runTests(baseContext, isRequest)

        @Suppress("RemoveRedundantQualifierName")
        fun toUnary() = RequesterTrial.Tester<Unit, B, R> { _, baseContext, isRequest ->
            runTests(baseContext, isRequest)
        }
    }

    fun interface ResultSupplier<T : Any, R : Any> {
        suspend fun TrialScope.supply(input: T?, isRequest: Boolean): R?

        suspend fun supplyIn(scope: TrialScope, input: T?, isRequest: Boolean) =
            scope.supply(input, isRequest)
    }
}