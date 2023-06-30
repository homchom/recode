package io.github.homchom.recode.feature

import io.github.homchom.recode.feature.rendering.FCodeSearch
import io.github.homchom.recode.feature.rendering.FSignRenderDistance
import io.github.homchom.recode.lifecycle.module

val RenderingFeatureGroup = module(featureGroupDetail("Rendering",
    FSignRenderDistance,
    FCodeSearch
))