@file:JvmName("ServerTrust")

package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.multiplayer.state.ipMatchesDF

// TODO: consider custom trusted servers? (perhaps not fitting for the mod though)
val isServerTrusted get() = ipMatchesDF