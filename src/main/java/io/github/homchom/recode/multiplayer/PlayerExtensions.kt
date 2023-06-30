package io.github.homchom.recode.multiplayer

import net.minecraft.world.entity.player.Player

val Player.username: String get() = gameProfile.name