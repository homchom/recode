package io.github.homchom.recode.init

/**
 * A **static** key to be passed to a singleton with a constructor, or to be otherwise used in
 * its initialization. Keys can only be [use]d once, which prevents the singleton from being
 * instantiated twice.
 */
class SingletonKey @StaticOnly constructor() {
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

/**
 * An opt-in annotation denoting that a constructor or function should only be invoked from a
 * static context.
 */
@RequiresOptIn("This should only be invoked from a static context and requires opt-in")
@Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION)
annotation class StaticOnly