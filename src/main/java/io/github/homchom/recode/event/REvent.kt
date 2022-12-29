package io.github.homchom.recode.event

import kotlinx.coroutines.flow.Flow

interface REvent<T> {
    val notifications: Flow<T>
}