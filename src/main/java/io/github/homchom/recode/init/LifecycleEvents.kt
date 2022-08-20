package io.github.homchom.recode.init

import io.github.homchom.recode.event.InvokableEvent
import io.github.homchom.recode.event.wrapEvent
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping
import net.minecraft.client.Minecraft

object ClientStopEvent :
    InvokableEvent<Minecraft, Unit, ClientStopping> by
        wrapEvent(ClientLifecycleEvents.CLIENT_STOPPING, { listener ->
            ClientStopping {
                listener(it, Unit)
            }
        })