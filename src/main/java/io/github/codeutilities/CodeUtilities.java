package io.github.codeutilities;

import com.google.gson.JsonParser;
import io.github.codeutilities.config.CodeUtilsConfig;
import io.github.codeutilities.cosmetics.CosmeticHandler;
import io.github.codeutilities.dfrpc.DFDiscordRPC;
import io.github.codeutilities.gui.menus.codeutils.ContributorsMenu;
import io.github.codeutilities.gui.menus.CustomHeadMenu;
import io.github.codeutilities.social.PlayerlistStarServer;
import io.github.codeutilities.template.TemplateStorageHandler;
import io.github.codeutilities.util.socket.SocketHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CodeUtilities implements ModInitializer {

    public static final String MOD_ID = "codeutilities";
    public static final String MOD_NAME = "CodeUtilities";
    public static final String MOD_VERSION = "2.2.1";
    public static final boolean BETA = true;

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Random RANDOM = new Random();
    public static final JsonParser JSON_PARSER = new JsonParser();
    public static final MinecraftClient MC = MinecraftClient.getInstance();
    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    public static Screen SCREEN_TO_OPEN;

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        Runtime.getRuntime().addShutdownHook(new Thread(this::onClose));

        /* register config
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
        AutoConfig.register(
                ModConfig.class,
                PartitioningSerializer.wrap(DummyConfigSerializer::new)
        );
         */

        // Initialize.
        CodeInitializer initializer = new CodeInitializer();
        CodeUtilsConfig.cacheConfig();
        initializer.add(new TemplateStorageHandler());
        initializer.add(new CustomHeadMenu());
        initializer.add(new ContributorsMenu());
        initializer.add(new DFDiscordRPC());
        initializer.add(new PlayerlistStarServer());

        // Initialize only if the config value is true.
        initializer.addIf(new SocketHandler(), CodeUtilsConfig.getBool("itemApi"));
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