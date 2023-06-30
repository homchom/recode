@file:JvmName("ServerStatus")

package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.multiplayer.state.ipMatchesDF
import net.minecraft.client.multiplayer.ServerData

// TODO: consider custom trusted servers? (perhaps not fitting for the mod though)
val ServerData?.isTrusted get() = ipMatchesDF