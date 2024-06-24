package io.github.homchom.recode.hypercube

import io.github.homchom.recode.game.getCompoundOrNull
import net.minecraft.nbt.CompoundTag

val CompoundTag.publicBukkitValues get() = getCompoundOrNull("PublicBukkitValues")