package io.github.homchom.recode.util

@RequiresOptIn("This inline class constructor is public for inlining but is essentially private; " +
        "use the appropriate factory function instead")
annotation class ExposedInline

@RequiresOptIn("This global declaration is public only for use by a specific Mixin (where interface " +
        "interjection was not feasible) and otherwise should not be used")
annotation class MixinPrivate