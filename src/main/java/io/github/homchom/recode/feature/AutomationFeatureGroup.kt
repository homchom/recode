package io.github.homchom.recode.feature

import io.github.homchom.recode.feature.automation.*
import io.github.homchom.recode.lifecycle.module

val AutomationFeatureGroup = module(featureGroupDetail("Automation",
    FAutoChatLocal, FAutoFly, FAutoLagSlayer, FAutoNightVision, FAutoTime, FAutoTip, FAutoResetCompact, FAutoWand
))