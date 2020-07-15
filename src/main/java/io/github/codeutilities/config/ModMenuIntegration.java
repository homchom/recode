package io.github.codeutilities.config;

import io.github.codeutilities.CodeUtilities;
import io.github.prospector.modmenu.ModMenu;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.client.MinecraftClient;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public String getModId() {
        return CodeUtilities.MOD_ID; // Return your modid here
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig
            .getConfigScreen(ModConfig.class, MinecraftClient.getInstance().currentScreen)
            .get();
    }
}
