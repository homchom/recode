package io.github.homchom.recode.util.mixin

import java.util.*

// TODO: test this
/**
 * An optimized wrapper for an [IdentityHashMap], used to augment custom fields into Minecraft classes with mixins.
 */
class MixinField<T : Any, V : Any>(private val default: () -> T) {
    private lateinit var valueMap: MutableMap<V, T>

    private var singletonInstance: V? = null // invalidated iff valueMap is initialized
    private var singletonValue: T? = default() // invalidated iff valueMap is initialized

    fun get(instance: V) = if (::valueMap.isInitialized) {
        valueMap.getOrPut(instance, default)
    } else {
        singletonValue!!
    }

    fun set(instance: V, value: T) {
        when {
            ::valueMap.isInitialized -> valueMap[instance] = value
            singletonInstance != null && instance != singletonInstance -> {
                valueMap = IdentityHashMap()
                valueMap[singletonInstance!!] = singletonValue!!
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