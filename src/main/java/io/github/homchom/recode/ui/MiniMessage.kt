package io.github.homchom.recode.ui

import net.kyori.adventure.platform.fabric.FabricClientAudiences
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.network.chat.Component

fun MiniMessage.deserializeToNative(input: String): Component {
    val adventureComponent = deserialize(input)
    return FabricClientAudiences.of().toNative(adventureComponent)
}