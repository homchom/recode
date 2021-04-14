package io.github.codeutilities.keybinds;

import io.github.codeutilities.config.ModConfig;
import net.minecraft.client.MinecraftClient;

public class FlightspeedToggle {
    public static boolean fs_is_normal = true;

    public static void toggleFlightspeed(String mode) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ModConfig.Keybinds_Flightspeed config = ModConfig.getConfig(ModConfig.Keybinds_Flightspeed.class);

        assert mc.player != null;
        if (fs_is_normal) {
            fs_is_normal = false;
            switch (mode) {
                case "medium":
                    mc.player.sendChatMessage("/fs " + config.fsMed);
                    return;
                case "fast":
                    mc.player.sendChatMessage("/fs " + config.fsFast);
            }
        } else mc.player.sendChatMessage("/fs " + config.fsNormal);
        fs_is_normal = true;
    }
}
