package io.github.homchom.recode.feature

import io.github.homchom.recode.Power
import io.github.homchom.recode.PowerCallback
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@RequiresOptIn("Feature registration irreversibly mutates global state and should only occur in " +
        "init blocks of object declarations")
annotation class AddsFeature

/**
 * Registers a feature with [builder].
 *
 * NOTE: Features have little special functionality at the moment, but should still be used as various refactors
 * progress. See the wiki for more information.
 *
 * @return This feature's [Power].
 */
@AddsFeature
@Suppress("UNUSED_PARAMETER")
inline fun registerFeature(name: String, builder: FeatureBuilder.() -> Unit): Power {
    val feature = FeatureBuilder().apply(builder)

    val power = feature.buildPower()
    // temporary
    @OptIn(DelicateCoroutinesApi::class)
    GlobalScope.launch { power.up() }

    return power
}

@AddsFeature
class FeatureBuilder {
    private val enableCallbacks = mutableListOf<PowerCallback>()
    private val disableCallbacks = mutableListOf<PowerCallback>()

    fun onEnable(callback: PowerCallback) {
        enableCallbacks += callback
    }

    fun onDisable(callback: PowerCallback) {
        disableCallbacks += callback
    }

    fun buildPower() = Power(
        onEnable = {
            for (callback in enableCallbacks) callback()
        },
        onDisable = {
            for (callback in disableCallbacks) callback()
        }
    )
}