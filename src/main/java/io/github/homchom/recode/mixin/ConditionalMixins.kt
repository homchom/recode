package io.github.homchom.recode.mixin

import net.fabricmc.loader.api.FabricLoader

// TODO: is there a better way to do all of this?

/**
 * Mixins with this annotation will only be applied if:
 * 1. The mod with ID [modID] is loaded.
 * 2. None of the mods with IDs in [disjointWith] are loaded.
 */
internal annotation class MixinConditional(val modID: String, val disjointWith: Array<String> = [])

internal val MixinConditional.shouldApplyMixin get() = when {
    !FabricLoader.getInstance().isModLoaded(modID) -> false
    disjointWith.any(FabricLoader.getInstance()::isModLoaded) -> false
    else -> true
}