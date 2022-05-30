package io.github.homchom.recode.mod.features.keybinds;

import io.github.homchom.recode.mod.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class FlightSpeedToggle {
    private final float normalFs = percentToFs(Config.getInteger("fsNormal"));

    public void toggleFlightSpeed(int percent) {
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null;

        float current = player.getAbilities().getFlyingSpeed();
        int target = current == normalFs ? percent : Config.getInteger("fsNormal");
        player.chat("/fs " + target);
    }

    // TODO: globalize or automate
    private float percentToFs(int percent) {
        return 0.05f * percent / 100;
    }
}
