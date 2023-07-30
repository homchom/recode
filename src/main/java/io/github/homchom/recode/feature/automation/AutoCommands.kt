package io.github.homchom.recode.feature.automation

import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.feature.FeatureModule
import io.github.homchom.recode.feature.featureModule
import io.github.homchom.recode.lifecycle.CoroutineModule
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.multiplayer.*
import io.github.homchom.recode.multiplayer.state.*
import kotlinx.coroutines.launch

// TODO: combine into one module per event after config is figured out

val FAutoChatLocal = autoCommand("chat local", DFStateDetectors) { (new) ->
    if (new is DFState.OnPlot && !currentDFState.isOnPlot(new.plot)) {
        if (Config.getBoolean("autoChatLocal") && new.session == null) {
            launch { ChatLocalRequester.request() }
        }
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
    if (new.mode is PlotMode.Dev && !currentDFState.isInMode(PlotMode.Dev)) {
        if (Config.getBoolean("autolagslayer")) {
            launch { LagSlayerRequesters.enable.request() }
        }
    }
}

val FAutoNightVision = autoCommand("nightvis", DFStateDetectors.ChangeMode) { (new) ->
    if (Config.getBoolean("autonightvis") && new.session != SupportSession.Helping) {
        if (new.mode != PlotMode.Play) {
            launch { NightVisionRequesters.enable.request() }
        }
    }
}

val FAutoResetCompact = autoCommand("resetcompact", DFStateDetectors.ChangeMode) { (new) ->
    if (Config.getBoolean("autoRC")) {
        if (new.mode is PlotMode.Dev) sendCommand("resetcompact")
    }
}

val FAutoTime = autoCommand("time", DFStateDetectors.ChangeMode) { (new) ->
    if (Config.getBoolean("autotime") && new.session != SupportSession.Helping) {
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
    if (Config.getBoolean("autowand") && new.session != SupportSession.Helping) {
        if (new.mode == PlotMode.Build) sendCommand("/wand")
    }
}

private inline fun <T> autoCommand(
    name: String,
    event: Listenable<T>,
    crossinline body: CoroutineModule.(T) -> Unit
): FeatureModule {
    return featureModule("Auto /$name") {
        onEnable {
            event.listenEach { body(it) }
        }
    }
}