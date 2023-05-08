package io.github.homchom.recode.server

import net.minecraft.world.entity.player.Player

val Player.username get() = gameProfile.name