@file:JvmName("ScreenEvents")

package io.github.homchom.recode.render

import io.github.homchom.recode.event.wrapFabricEvent
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen

val BeforeInitScreenEvent = wrapFabricEvent(BEFORE_INIT) { listener ->
    BeforeInit { client, screen, scaledWidth, scaledHeight ->
        listener(ScreenInitContext(client, screen, scaledWidth, scaledHeight))
    }
}

val AfterInitScreenEvent = wrapFabricEvent(AFTER_INIT) { listener ->
    AfterInit { client, screen, scaledWidth, scaledHeight ->
        listener(ScreenInitContext(client, screen, scaledWidth, scaledHeight))
    }
}

data class ScreenInitContext(
    val client: Minecraft,
    val screen: Screen,
    val scaledWidth: Int,
    val scaledHeight: Int
)

fun Screen.afterRender() = wrapFabricEvent(afterRender(this)) { listener ->
    AfterRender { screen, drawContext, mouseX, mouseY, tickDelta ->
        listener(ScreenRenderContext(screen, drawContext, mouseX, mouseY, tickDelta))
    }
}

data class ScreenRenderContext(
    val screen: Screen,
    val guiGraphics: GuiGraphics,
    val mouseX: Int,
    val mouseY: Int,
    val tickDelta: Float
)

fun Screen.onRemove() = wrapFabricEvent(remove(this)) { Remove(it) }