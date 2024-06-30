package io.github.homchom.recode.render

import com.mojang.authlib.GameProfile
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.client.player.RemotePlayer
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.world.entity.player.PlayerModelPart

/**
 * An entity to be used only for rendering arbitrary player skins statically.
 */
class StaticSkinRender(client: Minecraft, private val skin: PlayerSkin) : RemotePlayer(
    client.level!!,
    GameProfile(Util.NIL_UUID, "")
) {
    override fun getSkin() = skin
    override fun isModelPartShown(part: PlayerModelPart) = true
}