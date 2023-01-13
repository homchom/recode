package io.github.homchom.recode.util.collections

import io.github.homchom.recode.util.ExposedInline

@OptIn(ExposedInline::class)
fun <T> Array<T>.immutable() = ImmutableList(toList())

@OptIn(ExposedInline::class)
fun <T> Iterable<T>.immutable() = ImmutableList(toList())

@OptIn(ExposedInline::class)
fun <K, V> Map<K, V>.immutable() = ImmutableMap(toMap())

@OptIn(ExposedInline::class)
fun <T> Set<T>.immutable() = ImmutableSet(toSet())

@JvmInline
value class ImmutableList<out T> @ExposedInline constructor(private val list: List<T>) : List<T> by list

@JvmInline
value class ImmutableMap<K, out V> @ExposedInline constructor(private val map: Map<K, V>) : Map<K, V> by map

@JvmInline
value class ImmutableSet<out T> @ExposedInline constructor(private val set: Set<T>) : Set<T> by set