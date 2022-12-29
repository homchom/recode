package io.github.homchom.recode.render

import io.github.homchom.recode.lifecycle.module
import io.github.homchom.recode.mc

val CustomOutlineProcessor = module {
    onLoad {
        BeforeOutlineBlockEvent.listen {
            if (isEnabled) {
                val processor = mc.levelRenderer as OutlineProcessor
                if (processor.canProcessOutlines()) {
                    if (OutlineBlockEntityEvent.prevResult != null) {
                        processor.processOutlines(mc.frameTime)
                        mc.mainRenderTarget.bindWrite(false)
                    }
                }
            }
        }
    }
}

interface OutlineProcessor {
    fun canProcessOutlines(): Boolean
    fun processOutlines(partialTick: Float)
}