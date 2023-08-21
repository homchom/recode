package io.github.homchom.recode.mod.features.keybinds;

import io.github.homchom.recode.mod.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.Objects;

public class FlightSpeedToggle {
    public void toggleFlightSpeed(int percent) {
        LocalPlayer player = Minecraft.getInstance().player;
        Objects.requireNonNull(player);

        float current = player.getAbilities().getFlyingSpeed();
        int target = current == normalFlightSpeed() ? percent : Config.getInteger("fsNormal");
        player.connection.sendUnsignedCommand("fs " + target);
    }

    private float normalFlightSpeed() {
        return 0.05f * Config.getInteger("fsNormal") / 100;
    }
}
