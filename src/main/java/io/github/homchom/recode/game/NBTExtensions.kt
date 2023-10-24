@file:Suppress("unused")

package io.github.homchom.recode.game

import net.minecraft.nbt.CompoundTag

fun CompoundTag.getByteOrNull(key: String) = getNullable(key, this::getByte)

fun CompoundTag.getShortOrNull(key: String) = getNullable(key, this::getShort)

fun CompoundTag.getIntOrNull(key: String) = getNullable(key, this::getInt)

fun CompoundTag.getLongOrNull(key: String) = getNullable(key, this::getLong)

fun CompoundTag.getFloatOrNull(key: String) = getNullable(key, this::getFloat)

fun CompoundTag.getDoubleOrNull(key: String) = getNullable(key, this::getDouble)

fun CompoundTag.getStringOrNull(key: String) = getNullable(key, this::getString)

fun CompoundTag.getByteArrayOrNull(key: String) = getNullable(key, this::getByteArray)

fun CompoundTag.getIntArrayOrNull(key: String) = getNullable(key, this::getIntArray)

fun CompoundTag.getLongArrayOrNull(key: String) = getNullable(key, this::getLongArray)

fun CompoundTag.getCompoundOrNull(key: String) = getNullable(key, this::getCompound)

fun CompoundTag.getListOrNull(key: String, elementType: Byte) =
    if (contains(key)) getList(key, elementType.toInt()) else null

private inline fun <R : Any> CompoundTag.getNullable(key: String, getter: (String) -> R) =
    if (contains(key)) getter(key) else null