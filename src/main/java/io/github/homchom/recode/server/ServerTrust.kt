@file:JvmName("ServerTrust")

package io.github.homchom.recode.server

// TODO: consider custom trusted servers? (perhaps not fitting for the mod though)
val isServerTrusted get() = ipMatchesDF