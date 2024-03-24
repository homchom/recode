package io.github.homchom.recode.config

import io.github.homchom.recode.mod.config.LegacyConfig
import io.github.homchom.recode.mod.config.structure.ConfigManager
import io.github.homchom.recode.util.std.HeteroMap
import io.github.homchom.recode.util.std.MutableHeteroMap

// right now, this object just encapsulates LegacyConfig with a cache
// TODO: with config refactor, make it in control of the config file and menu
object Config {
    private val map = object : MutableHeteroMap<Setting<*>>() {
        operator fun <V : Any> get(key: Setting<V>) = getRaw(key)
    }

    private val updateMap = mutableMapOf<String, () -> Unit>()

    @JvmStatic
    operator fun <T : Any> get(setting: Setting<T>): T? {
        map[setting]?.let { return it }

        val value = tryPut(setting) ?: return null
        val string = setting.configString
        if (!updateMap.containsKey(string)) {
            updateMap[string] = { tryPut(setting) }
        }
        return value
    }

    fun save() {
        for (updater in updateMap.values) updater()
    }

    private fun <T : Any> tryPut(setting: Setting<T>): T? {
        val legacySetting = ConfigManager.getInstance().find(setting.configString)
        // see Config todo
        val legacyValue = LegacyConfig.getValue(legacySetting, Any::class.java) as T?
        if (legacyValue != null) map[setting] = legacyValue
        return legacyValue
    }
}

interface Setting<T : Any> : HeteroMap.Key<T> {
    val configString: String
}