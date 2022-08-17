package io.github.homchom.recode.render

import io.github.homchom.recode.event.CustomEvent
import io.github.homchom.recode.event.DependentEvent
import io.github.homchom.recode.event.ValidatedEvent
import io.github.homchom.recode.event.createEvent
import io.github.homchom.recode.ui.RGBAColor
import net.minecraft.world.level.block.entity.BlockEntity

object RenderBlockEntityEvent :
    CustomEvent<BlockEntity, Boolean> by createEvent(),
    ValidatedEvent<BlockEntity>

object OutlineBlockEntityEvent :
    CustomEvent<BlockEntity, OutlineResult> by DependentEvent(createEvent(), CustomOutlineProcessor)

class OutlineResult {
    var outlineColor: RGBAColor? = null
}