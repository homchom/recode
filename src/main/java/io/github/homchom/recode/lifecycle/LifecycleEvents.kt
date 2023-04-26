package io.github.homchom.recode.lifecycle

import io.github.homchom.recode.event.WrappedEvent
import io.github.homchom.recode.event.wrapFabricEvent
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping
import net.minecraft.client.Minecraft

object ClientStopEvent :
    WrappedEvent<Minecraft, ClientStopping> by
        wrapFabricEvent(ClientLifecycleEvents.CLIENT_STOPPING, { listener ->
            ClientStopping { listener(it) }
        })