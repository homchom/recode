package io.github.homchom.recode.feature.automation

import io.github.homchom.recode.Power
import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.event.request
import io.github.homchom.recode.feature.Feature
import io.github.homchom.recode.feature.feature
import io.github.homchom.recode.hypercube.*
import io.github.homchom.recode.hypercube.state.*
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.multiplayer.sendCommand
import kotlinx.coroutines.launch

object AutoCommands {
    init {
        autoCommand("chat local", DFStateDetectors) { (new) ->
            if (new is DFState.OnPlot && !currentDFState.isOnPlot(new.plot)) {
                if (Config.getBoolean("autoChatLocal") && new.session == null) {
                    launch { ChatLocalRequester.request() }
                }
            }
        }

        autoCommand("fly", DFStateDetectors.EnterSpawn) { (new) ->
            launch {
                if (Config.getBoolean("autofly") && DonorRank.NOBLE in new.permissions()) {
                    FlightRequesters.enable.request()
                }
            }
        }

        autoCommand("lagslayer", DFStateDetectors.ChangeMode) { (new) ->
            if (new.mode is PlotMode.Dev && !currentDFState.isInMode(PlotMode.Dev)) {
                if (Config.getBoolean("autolagslayer")) {
                    launch { LagSlayerRequesters.enable.request() }
                }
            }
        }

        autoCommand("nightvis", DFStateDetectors.ChangeMode) { (new) ->
            if (Config.getBoolean("autonightvis") && new.session != SupportSession.Helping) {
                if (new.mode != PlotMode.Play) {
                    launch { NightVisionRequesters.enable.request() }
                }
            }
        }

        autoCommand("resetcompact", DFStateDetectors.ChangeMode) { (new) ->
            if (Config.getBoolean("autoRC")) {
                if (new.mode is PlotMode.Dev) sendCommand("resetcompact")
            }
        }

        autoCommand("time", DFStateDetectors.ChangeMode) { (new) ->
            if (Config.getBoolean("autotime") && new.session != SupportSession.Helping) {
                if (new.mode != PlotMode.Play) {
                    launch { ClientTimeRequester.request(Config.getLong("autotimeval")) }
                }
            }
        }

        autoCommand("tip", JoinDFDetector) { info ->
            if (Config.getBoolean("autoTip") && info.canTip) {
                sendCommand("tip")
            }
        }

        autoCommand("/wand", DFStateDetectors.ChangeMode) { (new) ->
            if (Config.getBoolean("autowand") && new.session != SupportSession.Helping) {
                if (new.mode == PlotMode.Build) sendCommand("/wand")
            }
        }
    }
}

private inline fun <T> autoCommand(
    name: String,
    event: Listenable<T>,
    crossinline body: Power.(T) -> Unit
): Feature {
    return feature(name) {
        onEnable {
            event.listenEach { body(it) }
        }
    }
}