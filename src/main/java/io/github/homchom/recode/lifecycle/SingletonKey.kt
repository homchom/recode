package io.github.homchom.recode.lifecycle

/**
 * A "key" for class initialization that can be only used once. Useful for enforcing the singleton invariant of a
 * singleton with a constructor.
 */
class SingletonKey {
    private var wasUsed = false

    /**
     * Uses this key.
     *
     * @throws IllegalStateException if the key has already been used.
     */
    fun use() {
        check (!wasUsed) { "Keys can only be used to instantiate a singleton once" }
        wasUsed = true
    }
}