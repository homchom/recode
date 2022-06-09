package io.github.homchom.recode.feature

import io.github.homchom.recode.feature.rendering.FCodeSearch
import io.github.homchom.recode.feature.rendering.FSignRenderDistance

class RenderingFeatureGroup : FeatureGroup("Rendering") {
    override val features = listOf(
        FSignRenderDistance(),
        FCodeSearch()
    )

    override val dependencies = none()
}