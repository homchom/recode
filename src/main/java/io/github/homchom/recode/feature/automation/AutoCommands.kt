package io.github.homchom.recode.feature.automation

import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.feature.FeatureModule
import io.github.homchom.recode.feature.featureModule
import io.github.homchom.recode.lifecycle.CoroutineModule
import io.github.homchom.recode.logInfo
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.multiplayer.*
import io.github.homchom.recode.multiplayer.state.*
import kotlinx.coroutines.launch

// TODO: combine into one module per event after config is figured out

val FAutoChatLocal = autoCommand("chat local", DFStateDetectors) { (new) ->
    if (new is PlayState && currentDFState !is PlayState) {
        if (Config.getBoolean("autoChatLocal") && new.session == null) {
            logInfo("before /c l")
            ChatLocalRequester.request()
            logInfo("after /c l")
        }
    }
}

val FAutoFly = autoCommand("fly", DFStateDetectors.EnterSpawn) { (new) ->
    if (Config.getBoolean("autofly") && DonorRank.NOBLE in new.permissions()) {
        logInfo("before /fly")
        FlightRequesters.enable.request()
        logInfo("after /fly")
    }
}

val FAutoLagSlayer = autoCommand("lagslayer", DFStateDetectors.ChangeMode) { (new) ->
    if (new.mode == PlotMode.Dev && !currentDFState.isInMode(PlotMode.Dev)) {
        if (Config.getBoolean("autolagslayer")) {
            logInfo("before /lagslayer")
            LagSlayerRequesters.enable.request()
            logInfo("after /lagslayer")
        }
    }
}

val FAutoNightVision = autoCommand("nightvis", DFStateDetectors.ChangeMode) { (new) ->
    if (Config.getBoolean("autonightvis") && new.session != SupportSession.Helping) {
        if (new.mode != PlotMode.Play) {
            logInfo("before /nightvis")
            NightVisionRequesters.enable.request()
            logInfo("after /nightvis")
        }
    }
}

val FAutoResetCompact = autoCommand("resetcompact", DFStateDetectors.ChangeMode) { (new) ->
    if (Config.getBoolean("autoRC")) {
        if (new.mode == PlotMode.Dev) sendCommand("resetcompact")
    }
}

val FAutoTime = autoCommand("time", DFStateDetectors.ChangeMode) { (new) ->
    if (Config.getBoolean("autotime") && new.session != SupportSession.Helping) {
        if (new.mode != PlotMode.Play) {
            logInfo("before /time")
            ClientTimeRequester.request(Config.getLong("autotimeval"))
            logInfo("after /time")
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
    crossinline body: suspend CoroutineModule.(T) -> Unit
): FeatureModule {
    return featureModule("Auto /$name") {
        onEnable {
            event.listenEach { context ->
                launch {
                    // currently commented out
                    //delay(25.milliseconds) // https://github.com/PaperMC/Velocity/issues/909 TODO: remove
                    body(context)
                }
            }
        }
    }
}