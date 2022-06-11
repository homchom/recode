package io.github.homchom.recode.render

import io.github.homchom.recode.event.RecodeEvents
import io.github.homchom.recode.init.ModuleDefinition
import io.github.homchom.recode.init.RModule
import io.github.homchom.recode.mc
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents

@OptIn(GlobalUsesCustomOutlineProcessor::class)
class CustomOutlineProcessor : ModuleDefinition {
    override val dependencies = none()

    override fun RModule.onLoad() {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register { _, _ ->
            if (isEnabled) {
                val processor = mc.levelRenderer as OutlineProcessor
                if (processor.canProcessOutlines()) {
                    if (RecodeEvents.OUTLINE_BLOCK_ENTITY.prevResult != null) {
                        processor.processOutlines(mc.frameTime)
                        mc.mainRenderTarget.bindWrite(false)
                    }
                }
            }
            true
        }
    }

    override fun RModule.onEnable() = Unit
    override fun RModule.onDisable() = Unit
}

interface OutlineProcessor {
    fun canProcessOutlines(): Boolean
    fun processOutlines(partialTick: Float)
}

@RequiresOptIn("CustomOutlineProcessor must be enabled wherever this is used. Other " +
        "modules using this should add it as a dependency and opt in")
annotation class GlobalUsesCustomOutlineProcessor