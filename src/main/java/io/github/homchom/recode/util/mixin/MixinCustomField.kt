package io.github.homchom.recode.util.mixin

import com.google.common.collect.MapMaker

// TODO: test this
/**
 * An optimized wrapper for a weak identity hash map, used to augment custom fields into Minecraft classes
 * with mixins.
 * 1. For best performance, this should only be used at most once per mixin; use a class type when multiple
 * fields are desired.
 * 2. This is not necessary to use when the target class is reasonably assumed to be a singleton.
 */
class MixinCustomField<T, V : Any>(private val default: () -> T) {
    private lateinit var valueMap: MutableMap<V, T>

    private var singletonInstance: V? = null // invalidated iff valueMap is initialized
    private var singletonValue: T? = default() // invalidated iff valueMap is initialized

    fun get(instance: V) = if (::valueMap.isInitialized) {
        valueMap.getOrPut(instance, default)
    } else {
        singletonValue!!
    }

    @Suppress("UNCHECKED_CAST")
    fun set(instance: V, value: T) {
        when {
            ::valueMap.isInitialized -> valueMap[instance] = value
            singletonInstance != null && instance != singletonInstance -> {
                valueMap = MapMaker().weakKeys().makeMap() // identity-based
                valueMap[singletonInstance!!] = singletonValue as T
                valueMap[instance] = value
                singletonInstance = null
                singletonValue = null
            }
            else -> {
                singletonInstance = instance
                singletonValue = value
            }
        }
    }
}