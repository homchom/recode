package io.github.homchom.recode.feature.automation

import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.event.requestIn
import io.github.homchom.recode.feature.feature
import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.mod.features.LagslayerHUD
import io.github.homchom.recode.server.requests.ChatLocalRequester
import io.github.homchom.recode.server.requests.ClientTimeRequester
import io.github.homchom.recode.server.requests.NightVisionRequesters
import io.github.homchom.recode.server.sendCommand
import io.github.homchom.recode.server.state.DFStateDetectors
import io.github.homchom.recode.server.state.PlayState
import io.github.homchom.recode.server.state.PlotMode
import io.github.homchom.recode.sys.player.DFInfo

// TODO: combine into one module per event after config is figured out

val FAutoWand = autoCommand("/wand", DFStateDetectors.ChangeMode) { new ->
    if (Config.getBoolean("autowand")) {
        if (new().mode == PlotMode.Build) sendCommand("/wand")
    }
}

val FAutoChatLocal = autoCommand("chat local", DFStateDetectors) { new ->
    if (Config.getBoolean("autoChatLocal") && !DFInfo.currentState.isInSession) {
        if (new() is PlayState) ChatLocalRequester.requestIn(coroutineScope)
    }
}

val FAutoTime = autoCommand("time", DFStateDetectors.ChangeMode) { new ->
    if (Config.getBoolean("autotime") && !DFInfo.currentState.isInSession) {
        if (new().mode != PlotMode.Play) {
            ClientTimeRequester.requestIn(coroutineScope, Config.getLong("autotimeval"))
        }
    }
}

val FAutoNightVision = autoCommand("nightvis", DFStateDetectors.ChangeMode) { new ->
    if (Config.getBoolean("autonightvis") && !DFInfo.currentState.isInSession) {
        if (new().mode != PlotMode.Play) {
            NightVisionRequesters.enable.requestIn(coroutineScope)
        }
    }
}

val FAutoResetCompact = autoCommand("reset compact", DFStateDetectors.ChangeMode) { new ->
    if (Config.getBoolean("autoRC") && !DFInfo.currentState.isInSession) {
        if (new().mode == PlotMode.Dev) sendCommand("reset compact")
    }
}

val FAutoLagSlayer = autoCommand("lagslayer", DFStateDetectors.ChangeMode) { new ->
    if (Config.getBoolean("autolagslayer") && !new().isInSession) {
        if (!LagslayerHUD.lagSlayerEnabled) {
            // TODO: execute silently without ChatUtil
            if (new().mode == PlotMode.Dev) sendCommand("lagslayer")
        }
    }
}

private fun <T> autoCommand(name: String, event: Listenable<T>, body: ExposedModule.(T) -> Unit) =
    feature("Auto /$name") {
        onEnable {
            event.listenEach { body(it) }
        }
    }