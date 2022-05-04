package io.github.homchom.recode.mod.features.keybinds;

import io.github.homchom.recode.mod.config.Config;
import net.minecraft.client.Minecraft;

public class FlightspeedToggle {
    public static boolean fs_is_normal = true;

    public static void toggleFlightspeed(String mode) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;
        if (fs_is_normal) {
            fs_is_normal = false;
            switch (mode) {
                case "medium":
                    mc.player.chat("/fs " + Config.getInteger("fsMed"));
                    return;
                case "fast":
                    mc.player.chat("/fs " + Config.getInteger("fsFast"));
            }
        } else mc.player.chat("/fs " + Config.getInteger("fsNormal"));
        fs_is_normal = true;
    }
}
