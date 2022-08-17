package io.github.homchom.recode.render

import io.github.homchom.recode.init.weakModule
import io.github.homchom.recode.mc
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents

val CustomOutlineProcessor = weakModule {
    onLoad {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register { _, _ ->
            if (isEnabled) {
                val processor = mc.levelRenderer as OutlineProcessor
                if (processor.canProcessOutlines()) {
                    if (OutlineBlockEntityEvent.prevResult != null) {
                        processor.processOutlines(mc.frameTime)
                        mc.mainRenderTarget.bindWrite(false)
                    }
                }
            }
            true
        }
    }
}

interface OutlineProcessor {
    fun canProcessOutlines(): Boolean
    fun processOutlines(partialTick: Float)
}