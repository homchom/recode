package io.github.homchom.recode.feature

import io.github.homchom.recode.feature.automation.*
import io.github.homchom.recode.lifecycle.module

val AutomationFeatureGroup = module("automation feature group", featureGroup("Automation",
    FAutoChatLocal, FAutoFly, FAutoLagSlayer, FAutoNightVision, FAutoTime, FAutoTip, FAutoResetCompact, FAutoWand
))