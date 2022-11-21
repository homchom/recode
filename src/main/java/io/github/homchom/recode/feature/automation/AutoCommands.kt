package io.github.homchom.recode.feature.automation

import io.github.homchom.recode.feature.feature
import io.github.homchom.recode.server.ChangeDFStateEvent
import io.github.homchom.recode.server.sendCommand
import io.github.homchom.recode.server.state.PlayState
import io.github.homchom.recode.server.state.PlotMode

val FAutoWand = feature("Auto //wand") {
    onLoad {
        ChangeDFStateEvent.hook { (new) ->
            if (new is PlayState && new.mode == PlotMode.BUILD) sendCommand("/wand")
        }
    }
}