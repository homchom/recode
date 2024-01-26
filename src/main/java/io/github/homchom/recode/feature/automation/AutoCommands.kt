package io.github.homchom.recode.feature.automation

import io.github.homchom.recode.Power
import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.event.listenEach
import io.github.homchom.recode.feature.registerFeature
import io.github.homchom.recode.hypercube.CommandSenders
import io.github.homchom.recode.hypercube.JoinDFDetector
import io.github.homchom.recode.hypercube.state.*
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.multiplayer.send
import kotlinx.coroutines.launch

object AutoCommands {
    init {
        register("chat local", "autoChatLocal", DFStateDetectors) { (new) ->
            if (new is DFState.OnPlot && !currentDFState.isOnPlot(new.plot)) {
                if (new.session == null) launch { CommandSenders.ChatLocal.send() }
            }
        }

        register("fly", "autofly", DFStateDetectors.EnterSpawn) { (new) ->
            launch {
                if (DonorRank.NOBLE in new.permissions()) CommandSenders.Flight.enable.send()
            }
        }

        register("lagslayer", "autolagslayer", DFStateDetectors.ChangeMode) { (new) ->
            if (new.mode is PlotMode.Dev && !currentDFState.isInMode(PlotMode.Dev)) {
                launch { CommandSenders.LagSlayer.enable.send() }
            }
        }

        register("nightvis", "autonightvis", DFStateDetectors.ChangeMode) { (new) ->
            if (new.session != SupportSession.Helping && new.mode != PlotMode.Play) {
                launch { CommandSenders.NightVision.enable.send() }
            }
        }

        register("resetcompact", "autoRC", DFStateDetectors.ChangeMode) { (new) ->
            if (new.mode is PlotMode.Dev) {
                launch { CommandSenders.ResetCompact.send() }
            }
        }

        register("time", "autotime", DFStateDetectors.ChangeMode) { (new) ->
            if (new.session != SupportSession.Helping && new.mode != PlotMode.Play) {
                launch { CommandSenders.ClientTime.send(Config.getLong("autotimeval")) }
            }
        }

        register("tip", "autoTip", JoinDFDetector) { info ->
            if (info.canTip) {
                launch { CommandSenders.Tip.send() }
            }
        }

        register("/wand", "autowand", DFStateDetectors.ChangeMode) { (new) ->
            if (new.session != SupportSession.Helping && new.mode == PlotMode.Build) {
                launch { CommandSenders.Wand.send() }
            }
        }
    }

    private inline fun <T> register(
        name: String,
        configKey: String,
        event: Listenable<T>,
        crossinline body: Power.(T) -> Unit
    ) {
        registerFeature(name) {
            onEnable {
                listenEach(event) {
                    if (Config.getBoolean(configKey)) body(it)
                }
            }
        }
    }
}