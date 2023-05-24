@file:JvmName("ServerTrust")

package io.github.homchom.recode.server

import io.github.homchom.recode.server.state.ipMatchesDF

// TODO: consider custom trusted servers? (perhaps not fitting for the mod though)
val isServerTrusted get() = ipMatchesDF