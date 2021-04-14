package io.github.codeutilities;

import com.google.gson.JsonParser;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.cosmetics.CosmeticHandler;
import io.github.codeutilities.dfrpc.DFDiscordRPC;
import io.github.codeutilities.gui.CustomHeadMenu;
import io.github.codeutilities.template.TemplateStorageHandler;
import io.github.codeutilities.util.socket.SocketHandler;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CodeUtilities implements ModInitializer {

    public static final String MOD_ID = "codeutilities";
    public static final String MOD_NAME = "CodeUtilities";
    public static final String MOD_VERSION = "2.2.0";
    public static final boolean BETA = false;

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Random RANDOM = new Random();
    public static final JsonParser JSON_PARSER = new JsonParser();
    public static final MinecraftClient MC = MinecraftClient.getInstance();
    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
        Runtime.getRuntime().addShutdownHook(new Thread(this::onClose));

        // Initialize.
        CodeInitializer initializer = new CodeInitializer();
        initializer.add(new TemplateStorageHandler());
        initializer.add(new CustomHeadMenu());
        initializer.add(new DFDiscordRPC());

        // Initialize only if the config value is true.
        initializer.addIf(new SocketHandler(), ModConfig.getConfig(ModConfig.class).itemApi);
        MC.send(CosmeticHandler.INSTANCE::load);
    }

    public void onClose() {
        System.out.println("CLOSED");

        // Close all the services.
        TemplateStorageHandler.getInstance().save();
        CosmeticHandler.INSTANCE.shutdownExecutorService();
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

}