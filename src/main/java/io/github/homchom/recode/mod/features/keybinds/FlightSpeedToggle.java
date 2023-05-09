package io.github.homchom.recode.mod.features.keybinds;

import io.github.homchom.recode.mod.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.Objects;

public class FlightSpeedToggle {
    private final float normalFs = percentToFs(Config.getInteger("fsNormal"));

    public void toggleFlightSpeed(int percent) {
        LocalPlayer player = Minecraft.getInstance().player;
        Objects.requireNonNull(player);

        float current = player.getAbilities().getFlyingSpeed();
        int target = current == normalFs ? percent : Config.getInteger("fsNormal");
        player.connection.sendUnsignedCommand("fs " + target);
    }

    // TODO: globalize or automate
    private float percentToFs(int percent) {
        return 0.05f * percent / 100;
    }
}
