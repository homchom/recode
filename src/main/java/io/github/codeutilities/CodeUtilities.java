package io.github.codeutilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import io.github.codeutilities.mod.commands.CommandHandler;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.mod.config.internal.ConfigFile;
import io.github.codeutilities.mod.config.internal.ConfigInstruction;
import io.github.codeutilities.mod.config.internal.gson.ConfigSerializer;
import io.github.codeutilities.mod.config.internal.gson.types.BooleanSerializer;
import io.github.codeutilities.mod.config.internal.gson.types.DoubleSerializer;
import io.github.codeutilities.mod.config.internal.gson.types.DynamicStringSerializer;
import io.github.codeutilities.mod.config.internal.gson.types.EnumSerializer;
import io.github.codeutilities.mod.config.internal.gson.types.FloatSerializer;
import io.github.codeutilities.mod.config.internal.gson.types.IntegerSerializer;
import io.github.codeutilities.mod.config.internal.gson.types.LongSerializer;
import io.github.codeutilities.mod.config.internal.gson.types.SoundSerializer;
import io.github.codeutilities.mod.config.internal.gson.types.StringSerializer;
import io.github.codeutilities.mod.config.internal.gson.types.list.StringListSerializer;
import io.github.codeutilities.mod.config.structure.ConfigManager;
import io.github.codeutilities.mod.config.types.BooleanSetting;
import io.github.codeutilities.mod.config.types.DoubleSetting;
import io.github.codeutilities.mod.config.types.DynamicStringSetting;
import io.github.codeutilities.mod.config.types.EnumSetting;
import io.github.codeutilities.mod.config.types.FloatSetting;
import io.github.codeutilities.mod.config.types.IntegerSetting;
import io.github.codeutilities.mod.config.types.LongSetting;
import io.github.codeutilities.mod.config.types.SoundSetting;
import io.github.codeutilities.mod.config.types.StringSetting;
import io.github.codeutilities.mod.config.types.list.StringListSetting;
import io.github.codeutilities.mod.events.EventHandler;
import io.github.codeutilities.mod.events.interfaces.OtherEvents;
import io.github.codeutilities.mod.features.TemplatePeeker;
import io.github.codeutilities.mod.features.commands.HeadsMenu;
import io.github.codeutilities.mod.features.discordrpc.DFDiscordRPC;
import io.github.codeutilities.mod.features.social.cosmetics.CosmeticHandler;
import io.github.codeutilities.mod.features.social.tab.Client;
import io.github.codeutilities.sys.file.FileUtil;
import io.github.codeutilities.sys.hypercube.codeaction.ActionDump;
import io.github.codeutilities.sys.hypercube.templates.TemplateStorageHandler;
import io.github.codeutilities.sys.networking.State;
import io.github.codeutilities.sys.networking.websocket.SocketHandler;
import io.github.codeutilities.sys.util.LimitedHashmap;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CodeUtilities implements ModInitializer {

    public static final String MOD_ID = "codeutilities";
    public static final String MOD_NAME = "CodeUtilities";
    public static String MOD_VERSION;
    public static final boolean BETA = false; // todo: we're changing how "betas" work so this will need to be changed.

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
        .registerTypeAdapter(EnumSetting.class, new EnumSerializer())
        .registerTypeAdapter(DynamicStringSetting.class, new DynamicStringSerializer())
        .registerTypeAdapter(SoundSetting.class, new SoundSerializer())
        .setPrettyPrinting()
        .create();
    public static final JsonParser JSON_PARSER = new JsonParser();
    public static final MinecraftClient MC = MinecraftClient.getInstance();
    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final Path optionsTxtPath = FabricLoader.getInstance().getGameDir()
        .resolve("options.txt");

    public static String PLAYER_NAME = null;
    public static String PLAYER_UUID = null;
    public static String JEREMASTER_UUID = "6c669475-3026-4603-b3e7-52c97681ad3a";
    public static String RYANLAND_UUID = "3134fb4d-a345-4c5e-9513-97c2c951223e";
    public static String OPTIONSTXT = "";
    public static String CLIENT_LANG = "unknown";
    public static String[] signText = {};//stores the text of the code sign corresponding to the currently open chest
    public static ModelLoader modelLoader;
    public static LimitedHashmap<String, BakedModel> modelCache = new LimitedHashmap<>(256);

    static {
        try {
            OPTIONSTXT = FileUtil.readFile(optionsTxtPath.toString(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    @Override
    public void onInitialize() {
        MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion().getFriendlyString();
        log(Level.INFO, "Initializing");
//        Runtime.getRuntime().addShutdownHook(new Thread(this::onClose));
        System.setProperty("java.awt.headless", "false");
        //System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,TLSv1.3");

        // Get lang
        Pattern regex = Pattern.compile("\nlang:.*");
        Matcher m = regex.matcher(OPTIONSTXT);
        while (m.find()) {
            CLIENT_LANG = m.group(0).replaceAll("^\nlang:", "");
        }

        // Get player name
        PLAYER_NAME = MC.getSession().getUsername();
        PLAYER_UUID = MC.getSession().getUuid();

        // Initialize.
        CodeInitializer initializer = new CodeInitializer();
        initializer.add(new ConfigFile());
        initializer.add(new ConfigManager());
        initializer.add(new TemplateStorageHandler());
        initializer.add(new HeadsMenu());
        initializer.add(new DFDiscordRPC());
        initializer.add(new Client());
        initializer.add(new ActionDump());
        initializer.add(new EventHandler());
        initializer.add(new State.Locater());
        initializer.add(new CommandHandler());
        initializer.add(new TemplatePeeker());

        // Initializes only if the given condition is met. (this case: config value)
        // initializer.addIf(new AudioHandler(), Config.getBoolean("audio"));
        initializer.addIf(new SocketHandler(), Config.getBoolean("itemApi"));
        MC.send(CosmeticHandler.INSTANCE::load);

        ClientTickEvents.START_CLIENT_TICK
            .register(client -> OtherEvents.TICK.invoker().tick(client));

        log(Level.INFO, "Initialized successfully!");
    }

    public static void onClose() {
        LOGGER.info("Closing...");
        try {
            ConfigFile.getInstance().save();
            TemplateStorageHandler.getInstance().save();
            CosmeticHandler.INSTANCE.shutdownExecutorService();
        } catch (Exception err) {
            LOGGER.error("Error");
            err.printStackTrace();
        }
        LOGGER.info("Closed.");
    }

}