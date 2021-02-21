package io.github.codeutilities;

import com.google.gson.JsonParser;
import io.github.codeutilities.commands.CommandHandler;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.cosmetics.CosmeticHandler;
import io.github.codeutilities.gui.CustomHeadSearchGui;
import io.github.codeutilities.template.TemplateStorageHandler;
import io.github.codeutilities.template.*;
import io.github.codeutilities.util.socket.SocketHandler;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.OffsetDateTime;
import java.util.Random;

public class CodeUtilities implements ModInitializer {

    public static final String MOD_ID = "codeutilities";
    public static final String MOD_NAME = "CodeUtilities";
    public static final String MOD_VERSION = "v2.0.0-beta";

    public static final Logger LOGGER = LogManager.getLogger();
    public static final MinecraftClient MC = MinecraftClient.getInstance();
    public static final Random RANDOM = new Random();
    public static final JsonParser JSON_PARSER = new JsonParser();

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");

        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
        // Add a shutdown hook so we can save players template data on exit.
        Runtime.getRuntime().addShutdownHook(new Thread(this::onClose));

        // Initialize.
        CodeInitializer codeInitializer = new CodeInitializer();
        codeInitializer.initialize(new TemplateStorageHandler());
        codeInitializer.initialize(new CustomHeadSearchGui());

        if (ModConfig.getConfig().itemApi) {
            SocketHandler.init();
        }
    }

    public void onClose() {
        System.out.println("CLOSED");

        // Save all the templates.
        TemplateStorageHandler.getInstance().save();
        CosmeticHandler.shutdownExecutorService();
    }

    // This should be moved into its own class
    public static void openGuiAsync(LightweightGuiDescription gui) {
        new Thread(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MinecraftClient.getInstance().openScreen(new CottonClientScreen(gui));
        }).start();
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }
}