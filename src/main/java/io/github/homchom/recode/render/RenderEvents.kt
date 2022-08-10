package io.github.homchom.recode.render

import io.github.homchom.recode.event.*
import io.github.homchom.recode.ui.RGBAColor
import net.minecraft.world.level.block.entity.BlockEntity

object RenderBlockEntityEvent :
    CustomListenable<BlockEntity, Boolean> by createEvent(),
    ValidatedEvent<BlockEntity>


object OutlineBlockEntityEvent :
    CustomEvent<BlockEntity, OutlineResult> by DependentEvent(createEvent(), CustomOutlineProcessor)

class OutlineResult {
    var outlineColor: RGBAColor? = null
}