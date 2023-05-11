package io.github.homchom.recode.util.collections

import java.util.*
import java.util.concurrent.ConcurrentHashMap

fun <K, V> concurrentMap(): MutableMap<K, V> = ConcurrentHashMap<K, V>()

fun <E> concurrentSet(): MutableSet<E> = Collections.newSetFromMap(concurrentMap<E, _>())