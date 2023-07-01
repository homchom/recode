package io.github.homchom.recode.feature

import io.github.homchom.recode.feature.visual.FBuiltInResourcePacks
import io.github.homchom.recode.feature.visual.FCodeSearch
import io.github.homchom.recode.feature.visual.FSignRenderDistance
import io.github.homchom.recode.lifecycle.module

val VisualFeatureGroup = module(featureGroupDetail("Rendering",
    FBuiltInResourcePacks,
    FSignRenderDistance,
    FCodeSearch
))