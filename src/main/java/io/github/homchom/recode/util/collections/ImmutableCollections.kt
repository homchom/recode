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
value class ImmutableList<out E> @ExposedInline constructor(private val list: List<E>) : List<E> by list

@JvmInline
value class ImmutableMap<K, out V> @ExposedInline constructor(private val map: Map<K, V>) : Map<K, V> by map

@JvmInline
value class ImmutableSet<out E> @ExposedInline constructor(private val set: Set<E>) : Set<E> by set