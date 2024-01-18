package io.github.homchom.recode.util

@Target(AnnotationTarget.CONSTRUCTOR)
@RequiresOptIn("This exception type should generally only be caught, not thrown")
annotation class ThrownInternally