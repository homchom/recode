package io.github.homchom.recode.feature

import io.github.homchom.recode.feature.rendering.FCodeSearch
import io.github.homchom.recode.feature.rendering.FSignRenderDistance

object RenderingFeatureGroup : FeatureGroup by featureGroup("Rendering",
    FSignRenderDistance,
    FCodeSearch
)