package io.github.homchom.recode.util.std

/**
 * An abstract class for creating heterogeneous [Map]s, maps with more than one value type.
 *
 * This is abstract because a type-safe [Map.get] function must be declared separately, due to Java limitations.
 * When extending, use [getRaw] in your implementation.
 *
 * @param K The base type for keys, which implement [HeteroMap.Key].
 *
 * @see MutableHeteroMap
 */
abstract class HeteroMap<K : HeteroMap.Key<*>>(
    emptyDelegate: Map<K, Any> = mapOf()
) {
    /**
     * A generic key interface for [HeteroMap] and [MutableHeteroMap] keys.
     *
     * @param V the value type associated with the key.
     */
    interface Key<V : Any>

    /**
     * A [Key] with a [default] value.
     */
    interface DefaultedKey<V : Any> : Key<V> {
        fun default(): V
    }

    protected val delegate: MutableMap<Key<*>, Any> = emptyDelegate
        .also { require(it.isEmpty()) }
        .toMutableMap()

    /**
     * @see [Map.size]
     */
    val size by delegate::size

    /**
     * Gets the value associated with [key]. Note that this function is raw, in that [key] can be any
     * subtype of [Key], and therefore protected. Subclasses should expose this in a type-safe manner.
     */
    @Suppress("UNCHECKED_CAST")
    protected fun <V : Any> getRaw(key: Key<V>) = delegate[key]?.let { it as V }

    /**
     * Gets the value associated with [key], or the key's [DefaultedKey.default] value if a value does not exist.
     *
     * @see getRaw
     * @see Map.getOrDefault
     */
    protected fun <V : Any> getRawOrDefault(key: DefaultedKey<V>) = getRaw(key) ?: key.default()

    /**
     * @see [Map.isEmpty]
     */
    fun isEmpty() = delegate.isEmpty()

    /**
     * @see [Map.containsKey]
     */
    fun containsKey(key: K) = delegate.containsKey(key)

    /**
     * @see [Map.containsValue]
     */
    fun containsValue(value: Any) = delegate.containsValue(value)

    val keys by delegate::keys
}

/**
 * An abstract class for creating heterogeneous [MutableMap]s.
 *
 * @see HeteroMap
 */
abstract class MutableHeteroMap<K : HeteroMap.Key<*>>(
    emptyDelegate: Map<K, Any> = mapOf()
) : HeteroMap<K>(emptyDelegate) {
    /**
     * @see [MutableMap.put]
     */
    fun <V : Any> put(key: Key<V>, value: V) {
        delegate[key] = value
    }

    /**
     * @see [MutableMap.set]
     */
    operator fun <V : Any> set(key: Key<V>, value: V) = put(key, value)

    /**
     * Gets the value associated with [key], or the key's [DefaultedKey.default] value if a value does not exist.
     * Also ensures that the result is in the map.
     *
     * @see getRaw
     * @see MutableMap.getOrPut
     */
    protected fun <V : Any> getRawOrPut(key: DefaultedKey<V>) = getRaw(key)
        ?: key.default().also { put(key, it) }

    /**
     * @see [MutableMap.clear]
     */
    fun clear() = delegate.clear()
}