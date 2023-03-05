package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ExposedModule
import kotlinx.coroutines.flow.map

fun <T : Any, B, R : Any> trial(basis: Listenable<B>, tests: DetectorTrial.Tester<T, B, R>): DetectorTrial<T, R> =
    BasedDetectorTrial(basis, tests)

fun <T : Any, B, R : Any> trial(
    basis: Listenable<B>,
    start: suspend (input: T) -> Unit,
    tests: RequesterTrial.Tester<T, B, R>
): RequesterTrial<T, R> {
    return shortCircuitTrial(basis, { start(it); null }, tests)
}

fun <T : Any, B, R : Any> shortCircuitTrial(
    basis: Listenable<B>,
    start: suspend (input: T) -> R?,
    tests: RequesterTrial.Tester<T, B, R>
): RequesterTrial<T, R> {
    return BasedRequesterTrial(basis, start, tests)
}

fun <B, R : Any> nullaryTrial(
    basis: Listenable<B>,
    tests: DetectorTrial.NullaryTester<B, R>
): DetectorTrial<Unit, R> {
    return trial(basis, tests.toUnary())
}

fun <B, R : Any> nullaryTrial(
    basis: Listenable<B>,
    start: suspend () -> Unit,
    tests: RequesterTrial.NullaryTester<B, R>
): RequesterTrial<Unit, R> {
    return trial(basis, { start() }, tests.toUnary())
}

private class BasedDetectorTrial<T : Any, B, R : Any>(
    override val basis: Listenable<B>,
    private val tests: DetectorTrial.Tester<T, B, R>
) : DetectorTrial<T, R> {
    override fun supplyResultsFrom(module: ExposedModule) =
        FlowListenable(basis.getNotificationsFrom(module).map { baseContext ->
            DetectorTrial.ResultSupplier<T, R> { input ->
                tests.runTestsIn(this, input, baseContext)
            }
        })
}

private class BasedRequesterTrial<T : Any, B, R : Any>(
    override val basis: Listenable<B>,
    override val start: suspend (input: T) -> R?,
    private val tests: RequesterTrial.Tester<T, B, R>
): RequesterTrial<T, R> {
    override fun supplyResultsFrom(module: ExposedModule) =
        FlowListenable(basis.getNotificationsFrom(module).map { baseContext ->
            RequesterTrial.ResultSupplier<T, R> { input, isRequest ->
                tests.runTestsIn(this, input, baseContext, isRequest)
            }
        })
}