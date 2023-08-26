package io.github.homchom.recode.feature

import io.github.homchom.recode.Power
import io.github.homchom.recode.PowerCallback
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

sealed interface Configurable {
    /**
     * The name to be displayed in config-related UI.
     */
    val configName: String
}

/**
 * Builds a [Feature] with [builder].
 *
 * NOTE: Features have no special functionality at the moment, but should still be used as various refactors
 * progress. See the wiki for more information.
 */
fun feature(name: String, builder: FeatureBuilder.() -> Unit) = FeatureBuilder(name).apply(builder).build()

class FeatureBuilder(private val name: String) {
    private val enableCallbacks = mutableListOf<PowerCallback>()
    private val disableCallbacks = mutableListOf<PowerCallback>()

    fun onEnable(callback: PowerCallback) {
        enableCallbacks += callback
    }

    fun onDisable(callback: PowerCallback) {
        disableCallbacks += callback
    }

    fun build(): Feature {
        val power = Power(
            onEnable = {
                for (callback in enableCallbacks) callback()
            },
            onDisable = {
                for (callback in disableCallbacks) callback()
            }
        )

        return Feature(name, power)
    }
}

/**
 * @see feature
 */
class Feature(override val configName: String, power: Power) : Configurable {
    // temporary
    init {
        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope.launch { power.up() }
    }
}