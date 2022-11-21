@file:JvmName("ServerTrust")

package io.github.homchom.recode.server

import io.github.homchom.recode.server.state.isOnDF

// TODO: consider custom trusted servers? (perhaps not fitting for the mod though)
val isServerTrusted get() = isOnDF