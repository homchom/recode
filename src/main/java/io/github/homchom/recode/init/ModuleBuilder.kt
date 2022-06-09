package io.github.homchom.recode.init

sealed interface ModuleBuilder {
    fun load(module: ModuleDefinition): RModule

    fun setup(module: RModule) {
        // TODO: set up stuff like commands here
    }
}