package io.github.homchom.recode.feature.automation

import io.github.homchom.recode.Power
import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.event.listenEach
import io.github.homchom.recode.event.request
import io.github.homchom.recode.feature.registerFeature
import io.github.homchom.recode.hypercube.*
import io.github.homchom.recode.hypercube.state.*
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.multiplayer.sendCommand
import kotlinx.coroutines.launch

object AutoCommands {
    init {
        registerAutoCommand("chat local", DFStateDetectors) { (new) ->
            if (new is DFState.OnPlot && !currentDFState.isOnPlot(new.plot)) {
                if (Config.getBoolean("autoChatLocal") && new.session == null) {
                    launch { ChatLocalRequester.request() }
                }
            }
        }

        registerAutoCommand("fly", DFStateDetectors.EnterSpawn) { (new) ->
            launch {
                if (Config.getBoolean("autofly") && DonorRank.NOBLE in new.permissions()) {
                    FlightRequesters.enable.request()
                }
            }
        }

        registerAutoCommand("lagslayer", DFStateDetectors.ChangeMode) { (new) ->
            if (new.mode is PlotMode.Dev && !currentDFState.isInMode(PlotMode.Dev)) {
                if (Config.getBoolean("autolagslayer")) {
                    launch { LagSlayerRequesters.enable.request() }
                }
            }
        }

        registerAutoCommand("nightvis", DFStateDetectors.ChangeMode) { (new) ->
            if (Config.getBoolean("autonightvis") && new.session != SupportSession.Helping) {
                if (new.mode != PlotMode.Play) {
                    launch { NightVisionRequesters.enable.request() }
                }
            }
        }

        registerAutoCommand("resetcompact", DFStateDetectors.ChangeMode) { (new) ->
            if (Config.getBoolean("autoRC")) {
                if (new.mode is PlotMode.Dev) sendCommand("resetcompact")
            }
        }

        registerAutoCommand("time", DFStateDetectors.ChangeMode) { (new) ->
            if (Config.getBoolean("autotime") && new.session != SupportSession.Helping) {
                if (new.mode != PlotMode.Play) {
                    launch { ClientTimeRequester.request(Config.getLong("autotimeval")) }
                }
            }
        }

        registerAutoCommand("tip", JoinDFDetector) { info ->
            if (Config.getBoolean("autoTip") && info.canTip) {
                sendCommand("tip")
            }
        }

        registerAutoCommand("/wand", DFStateDetectors.ChangeMode) { (new) ->
            if (Config.getBoolean("autowand") && new.session != SupportSession.Helping) {
                if (new.mode == PlotMode.Build) sendCommand("/wand")
            }
        }
    }
}

private inline fun <T> registerAutoCommand(
    name: String,
    event: Listenable<T>,
    crossinline body: Power.(T) -> Unit
) {
    registerFeature(name) {
        onEnable {
            listenEach(event) { body(it) }
        }
    }
}