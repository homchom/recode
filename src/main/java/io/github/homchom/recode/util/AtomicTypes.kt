package io.github.homchom.recode.util

import java.util.concurrent.atomic.AtomicBoolean

/**
 * @see AtomicBoolean.getAndSet
 */
fun AtomicBoolean.getAndInvert(): Boolean {
    while (true) {
        val value = get()
        if (compareAndSet(value, !value)) return value
    }
}