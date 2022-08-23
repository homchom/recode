package io.github.homchom.recode.util

import java.util.Collections

fun <T> List<T>.unmodifiable(): List<T> = Collections.unmodifiableList(this)
fun <K, V> Map<out K, V>.unmodifiable(): Map<K, V> = Collections.unmodifiableMap(this)
fun <T> Set<T>.unmodifiable(): Set<T> = Collections.unmodifiableSet(this)
fun <T> Collection<T>.unmodifiable(): Collection<T> = Collections.unmodifiableCollection(this)