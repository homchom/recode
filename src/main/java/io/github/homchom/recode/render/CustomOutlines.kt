package io.github.homchom.recode.render

import io.github.homchom.recode.event.CustomHook
import io.github.homchom.recode.event.DependentHook
import io.github.homchom.recode.event.createHook
import io.github.homchom.recode.mc
import io.github.homchom.recode.ui.RGBAColor
import io.github.homchom.recode.util.MutableCase
import kotlinx.coroutines.withContext
import net.minecraft.world.level.block.entity.BlockEntity

object OutlineBlockEntityEvent :
    CustomHook<BlockEntity, MutableCase<RGBAColor>> by DependentHook(createHook(), {
        onEnable {
            BeforeOutlineBlockEvent.listenEach { context ->
                val processor = context.worldRenderContext.worldRenderer() as OutlineProcessor
                if (processor.canProcessOutlines()) {
                    if (OutlineBlockEntityEvent.prevResult != null) {
                        withContext(context.coroutineContext) {
                            processor.processOutlines(mc.frameTime)
                        }
                        mc.mainRenderTarget.bindWrite(false)
                    }
                }
            }
        }
    })

interface OutlineProcessor {
    fun canProcessOutlines(): Boolean
    fun processOutlines(partialTick: Float)
}