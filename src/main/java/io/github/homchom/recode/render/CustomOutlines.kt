package io.github.homchom.recode.render

import io.github.homchom.recode.event.*
import io.github.homchom.recode.mc
import io.github.homchom.recode.ui.RGBAColor
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.MutableCase
import kotlinx.coroutines.withContext
import net.minecraft.world.level.block.entity.BlockEntity

object OutlineBlockEntityEvent :
    CustomEvent<BlockEntityOutlineContext, Case<RGBAColor?>> by DependentEvent(
        createEvent { Case(outlineColor) },
        {
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
        }
    )

data class BlockEntityOutlineContext(val blockEntity: BlockEntity, var outlineColor: RGBAColor? = null)

interface OutlineProcessor {
    fun canProcessOutlines(): Boolean
    fun processOutlines(partialTick: Float)
}