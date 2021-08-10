package io.github.codeutilities.sys.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class SoundUtil {
    public static void playSound(SoundEvent soundEvent) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (mc.world != null && player != null) {
            mc.world.playSound(
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    soundEvent,
                    SoundCategory.PLAYERS,
                    1f,
                    1f,
                    false
            );
        }
    }
}
