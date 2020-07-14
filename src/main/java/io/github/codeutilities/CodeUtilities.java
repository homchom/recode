package io.github.codeutilities;

import io.github.codeutilities.config.ModConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CodeUtilities implements ModInitializer {

    public static final String MOD_ID = "codeutilities";
    public static final String MOD_NAME = "CodeUtilities";
    public static final String VERSION = "2.0";
    public static Logger LOGGER = LogManager.getLogger();

    public static MinecraftClient mc = MinecraftClient.getInstance();

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    public static void chat(String text) {
        mc.player.sendMessage(new LiteralText(text), false);
    }

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
    }

    public static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static boolean isOnDF() {
        if(mc.getCurrentServerEntry() == null) return false;
        return mc.getCurrentServerEntry().address.contains("mcdiamondfire.com");
    }

    public static void giveCreativeItem(ItemStack item) {
        assert mc.interactionManager != null;
        assert mc.player != null;
        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.getSwappableHotbarSlot());
    }

}