package io.github.homchom.recode.feature.automation

import io.github.homchom.recode.event.REvent
import io.github.homchom.recode.feature.feature
import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.mod.features.LagslayerHUD
import io.github.homchom.recode.server.ChangeDFStateEvent
import io.github.homchom.recode.server.invoke
import io.github.homchom.recode.server.requestIn
import io.github.homchom.recode.server.requests.ChatLocal
import io.github.homchom.recode.server.requests.ClientTime
import io.github.homchom.recode.server.requests.NightVision
import io.github.homchom.recode.server.sendCommand
import io.github.homchom.recode.server.state.PlayState
import io.github.homchom.recode.server.state.PlotMode
import io.github.homchom.recode.sys.player.DFInfo
import kotlinx.coroutines.launch

// TODO: combine into one module per event after config is figured out

val FAutoWand = autoCommand("/wand", ChangeDFStateEvent) { (new) ->
    if (Config.getBoolean("autowand")) {
        if (new is PlayState && new.mode == PlotMode.Build) sendCommand("/wand")
    }
}

val FAutoChatLocal = autoCommand("chat local", ChangeDFStateEvent) { (new) ->
    if (Config.getBoolean("autoChatLocal") && !DFInfo.currentState.isInSession) {
        if (new is PlayState) ChatLocal.requestIn(coroutineScope)
    }
}

val FAutoTime = autoCommand("time", ChangeDFStateEvent) { (new) ->
    if (Config.getBoolean("autotime") && !DFInfo.currentState.isInSession) {
        if (new is PlayState && new.mode != PlotMode.Play) {
            ClientTime.requestIn(coroutineScope, Config.getLong("autotimeval"))
        }
    }
}

val FAutoNightVision = autoCommand("nightvis", ChangeDFStateEvent) { (new) ->
    if (Config.getBoolean("autonightvis") && !DFInfo.currentState.isInSession) {
        if (new is PlayState && new.mode != PlotMode.Play) {
            coroutineScope.launch { NightVision.enable() }
        }
    }
}

val FAutoResetCompact = autoCommand("reset compact", ChangeDFStateEvent) { (new) ->
    if (Config.getBoolean("autoRC") && !DFInfo.currentState.isInSession) {
        if (new is PlayState && new.mode == PlotMode.Dev) sendCommand("reset compact")
    }
}

val FAutoLagSlayer = autoCommand("lagslayer", ChangeDFStateEvent) { (new) ->
    if (Config.getBoolean("autolagslayer") && !DFInfo.currentState.isInSession) {
        if (!LagslayerHUD.lagSlayerEnabled) {
            // TODO: execute silently without ChatUtil
            if (new is PlayState && new.mode == PlotMode.Dev) sendCommand("lagslayer")
        }
    }
}

private fun <C> autoCommand(name: String, event: REvent<C, *>, body: ExposedModule.(C) -> Unit) =
    feature("Auto /$name") {
        onLoad {
            event.hook { body(it) }
        }
    }