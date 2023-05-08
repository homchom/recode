package io.github.homchom.recode.sys.util;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class SoundUtil {
    public static void playSound(SoundEvent soundEvent) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (mc.level != null && player != null) {
            mc.level.playLocalSound(
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    soundEvent,
                    SoundSource.PLAYERS,
                    1f,
                    1f,
                    false
            );
        }
    }
}
