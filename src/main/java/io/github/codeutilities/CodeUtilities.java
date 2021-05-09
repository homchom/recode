package io.github.codeutilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import io.github.codeutilities.config.CodeUtilsConfig;
import io.github.codeutilities.config.internal.ConfigFile;
import io.github.codeutilities.config.internal.ConfigInstruction;
import io.github.codeutilities.config.internal.gson.ConfigSerializer;
import io.github.codeutilities.config.internal.gson.types.*;
import io.github.codeutilities.config.internal.gson.types.list.StringListSerializer;
import io.github.codeutilities.config.structure.ConfigManager;
import io.github.codeutilities.config.types.*;
import io.github.codeutilities.config.types.list.StringListSetting;
import io.github.codeutilities.features.external.AudioHandler;
import io.github.codeutilities.features.external.DFDiscordRPC;
import io.github.codeutilities.features.social.cosmetics.CosmeticHandler;
import io.github.codeutilities.gui.menus.CustomHeadMenu;
import io.github.codeutilities.util.networking.socket.SocketHandler;
import io.github.codeutilities.util.templates.TemplateStorageHandler;
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
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ConfigInstruction.class, new ConfigSerializer())
            .registerTypeAdapter(BooleanSetting.class, new BooleanSerializer())
            .registerTypeAdapter(IntegerSetting.class, new IntegerSerializer())
            .registerTypeAdapter(DoubleSetting.class, new DoubleSerializer())
            .registerTypeAdapter(FloatSetting.class, new FloatSerializer())
            .registerTypeAdapter(LongSetting.class, new LongSerializer())
            .registerTypeAdapter(StringSetting.class, new StringSerializer())
            .registerTypeAdapter(StringListSetting.class, new StringListSerializer())
            .setPrettyPrinting()
            .create();
    public static final JsonParser JSON_PARSER = new JsonParser();
    public static final MinecraftClient MC = MinecraftClient.getInstance();
    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    public static Screen SCREEN_TO_OPEN;

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        Runtime.getRuntime().addShutdownHook(new Thread(this::onClose));

        // Initialize.
        CodeInitializer initializer = new CodeInitializer();
        log(Level.INFO, "1");
        initializer.add(new ConfigFile());
        log(Level.INFO, "2");
        initializer.add(new ConfigManager());
        log(Level.INFO, "3");
        initializer.add(new TemplateStorageHandler());
        log(Level.INFO, "4");
        initializer.add(new CustomHeadMenu());
        log(Level.INFO, "5");
        initializer.add(new DFDiscordRPC());
        //initializer.add(new PlayerlistStarServer());
        log(Level.INFO, "6");

        // Initializes only if the given condition is met. (this case: config value)
        initializer.addIf(new AudioHandler(), CodeUtilsConfig.getBoolean("audio"));
        log(Level.INFO, "7");
        initializer.addIf(new SocketHandler(), CodeUtilsConfig.getBoolean("itemApi"));
        log(Level.INFO, "8");
        MC.send(CosmeticHandler.INSTANCE::load);
    }

    public void onClose() {
        System.out.println("CLOSED");
        ConfigFile.getInstance().save();
        TemplateStorageHandler.getInstance().save();
        CosmeticHandler.INSTANCE.shutdownExecutorService();
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

}