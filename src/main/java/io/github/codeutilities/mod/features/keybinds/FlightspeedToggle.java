package io.github.codeutilities.mod.features.keybinds;

import io.github.codeutilities.mod.config.Config;
import net.minecraft.client.MinecraftClient;

public class FlightspeedToggle {
    public static boolean fs_is_normal = true;

    public static void toggleFlightspeed(String mode) {
        MinecraftClient mc = MinecraftClient.getInstance();

        assert mc.player != null;
        if (fs_is_normal) {
            fs_is_normal = false;
            switch (mode) {
                case "medium":
                    mc.player.sendChatMessage("/fs " + Config.getInteger("fsMed"));
                    return;
                case "fast":
                    mc.player.sendChatMessage("/fs " + Config.getInteger("fsFast"));
            }
        } else mc.player.sendChatMessage("/fs " + Config.getInteger("fsNormal"));
        fs_is_normal = true;
    }
}
