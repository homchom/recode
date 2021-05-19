package io.github.codeutilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import io.github.codeutilities.config.Config;
import io.github.codeutilities.config.internal.ConfigFile;
import io.github.codeutilities.config.internal.ConfigInstruction;
import io.github.codeutilities.config.internal.gson.ConfigSerializer;
import io.github.codeutilities.config.internal.gson.types.*;
import io.github.codeutilities.config.internal.gson.types.list.StringListSerializer;
import io.github.codeutilities.config.structure.ConfigManager;
import io.github.codeutilities.config.types.*;
import io.github.codeutilities.config.types.list.StringListSetting;
import io.github.codeutilities.events.EventHandler;
import io.github.codeutilities.features.external.DFDiscordRPC;
import io.github.codeutilities.features.social.chat.ConversationTimer;
import io.github.codeutilities.features.social.cosmetics.CosmeticHandler;
import io.github.codeutilities.modules.Module;
import io.github.codeutilities.modules.actions.Action;
import io.github.codeutilities.modules.triggers.Trigger;
import io.github.codeutilities.util.file.FileUtil;
import io.github.codeutilities.util.gui.menus.CustomHeadMenu;
import io.github.codeutilities.util.networking.socket.SocketHandler;
import io.github.codeutilities.util.templates.TemplateStorageHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeUtilities implements ModInitializer {

    public static final String MOD_ID = "codeutilities";
    public static final String MOD_NAME = "CodeUtilities";
    public static final String MOD_VERSION = "2.2.2";
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

    private static final Path optionsTxtPath = FabricLoader.getInstance().getGameDir().resolve("options.txt");
    public static String OPTIONSTXT = "";
    public static String CLIENT_LANG = "unknown";

    static {
        try {
            OPTIONSTXT = FileUtil.readFile(optionsTxtPath.toString(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        Runtime.getRuntime().addShutdownHook(new Thread(this::onClose));

        // Get lang
        Pattern regex = Pattern.compile("\nlang:.*");
        Matcher m = regex.matcher(OPTIONSTXT);
        while (m.find()) CLIENT_LANG = m.group(0).replaceAll("^\nlang:", "");

        // Load modules
        Action.cacheActions();
        Trigger.cacheTriggers();
        Module.loadModules();

        // Initialize.
        CodeInitializer initializer = new CodeInitializer();
        initializer.add(new ConfigFile());
        initializer.add(new ConfigManager());
        initializer.add(new TemplateStorageHandler());
        initializer.add(new CustomHeadMenu());
        initializer.add(new DFDiscordRPC());
        initializer.add(new ConversationTimer());
        initializer.add(new EventHandler());
        //initializer.add(new PlayerlistStarServer());

        // Initializes only if the given condition is met. (this case: config value)
//        initializer.addIf(new AudioHandler(), Config.getBoolean("audio"));
        initializer.addIf(new SocketHandler(), Config.getBoolean("itemApi"));
        MC.send(CosmeticHandler.INSTANCE::load);

        log(Level.INFO, "Initialized successfully!");
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