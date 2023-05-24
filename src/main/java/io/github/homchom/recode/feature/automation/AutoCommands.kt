package io.github.homchom.recode.feature.automation

import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.feature.FeatureModule
import io.github.homchom.recode.feature.feature
import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.server.*
import io.github.homchom.recode.server.state.DFStateDetectors
import io.github.homchom.recode.server.state.DonorRank
import io.github.homchom.recode.server.state.PlayState
import io.github.homchom.recode.server.state.PlotMode
import kotlinx.coroutines.launch

// TODO: combine into one module per event after config is figured out

val FAutoChatLocal = autoCommand("chat local", DFStateDetectors) { (new) ->
    if (Config.getBoolean("autoChatLocal") /*&& !new.isInSession*/) {
        if (new is PlayState) launch { ChatLocalRequester.request() }
    }
}

val FAutoFly = autoCommand("fly", DFStateDetectors.EnterSpawn) { (new) ->
    launch {
        if (Config.getBoolean("autofly") && DonorRank.NOBLE in new.permissions()) {
            FlightRequesters.enable.request()
        }
    }
}

val FAutoLagSlayer = autoCommand("lagslayer", DFStateDetectors.ChangeMode) { (new) ->
    if (Config.getBoolean("autolagslayer") /*&& !new.isInSession*/) {
        if (new.mode == PlotMode.Dev) {
            launch { LagSlayerRequesters.enable.request() }
        }
    }
}

val FAutoNightVision = autoCommand("nightvis", DFStateDetectors.ChangeMode) { (new) ->
    if (Config.getBoolean("autonightvis") /*&& !new.isInSession*/) {
        if (new.mode != PlotMode.Play) {
            launch { NightVisionRequesters.enable.request() }
        }
    }
}

val FAutoResetCompact = autoCommand("resetcompact", DFStateDetectors.ChangeMode) { (new) ->
    if (Config.getBoolean("autoRC") /*&& !new.isInSession*/) {
        if (new.mode == PlotMode.Dev) sendCommand("resetcompact")
    }
}

val FAutoTime = autoCommand("time", DFStateDetectors.ChangeMode) { (new) ->
    if (Config.getBoolean("autotime") /*&& !new.isInSession*/) {
        if (new.mode != PlotMode.Play) {
            launch { ClientTimeRequester.request(Config.getLong("autotimeval")) }
        }
    }
}

val FAutoTip = autoCommand("tip", JoinDFDetector) { info ->
    if (Config.getBoolean("autoTip") && info.canTip) {
        sendCommand("tip")
    }
}

val FAutoWand = autoCommand("/wand", DFStateDetectors.ChangeMode) { (new) ->
    if (Config.getBoolean("autowand")) {
        if (new.mode == PlotMode.Build) sendCommand("/wand")
    }
}

private inline fun <T> autoCommand(
    name: String,
    event: Listenable<T>,
    crossinline body: ExposedModule.(T) -> Unit
): FeatureModule {
    return feature("Auto /$name") {
        onEnable {
            event.listenEach { body(it) }
        }
    }
}