package io.github.homchom.recode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.homchom.recode.mod.commands.CommandHandler;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.config.internal.ConfigFile;
import io.github.homchom.recode.mod.config.internal.ConfigInstruction;
import io.github.homchom.recode.mod.config.internal.gson.ConfigSerializer;
import io.github.homchom.recode.mod.config.internal.gson.types.*;
import io.github.homchom.recode.mod.config.internal.gson.types.list.StringListSerializer;
import io.github.homchom.recode.mod.config.structure.ConfigManager;
import io.github.homchom.recode.mod.config.types.*;
import io.github.homchom.recode.mod.config.types.list.StringListSetting;
import io.github.homchom.recode.mod.events.EventHandler;
import io.github.homchom.recode.mod.events.interfaces.OtherEvents;
import io.github.homchom.recode.mod.features.discordrpc.DFDiscordRPC;
import io.github.homchom.recode.mod.features.social.cosmetics.CosmeticHandler;
import io.github.homchom.recode.sys.file.FileUtil;
import io.github.homchom.recode.sys.hypercube.codeaction.ActionDump;
import io.github.homchom.recode.sys.hypercube.templates.TemplateStorageHandler;
import io.github.homchom.recode.sys.networking.State;
import io.github.homchom.recode.sys.networking.websocket.SocketHandler;
import io.github.homchom.recode.sys.util.LimitedHashmap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Recode implements ModInitializer {
    public static final String MOD_ID = "recode";
    public static final String MOD_NAME = "recode";
    public static final boolean BETA = false; // todo: we're changing how "betas" work so this will need to be changed.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
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
    public static final Minecraft MC = Minecraft.getInstance();
    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static Path optionsTxtPath;
    public static String MOD_VERSION;
    public static String PLAYER_NAME = null;
    public static String PLAYER_UUID = null;
    public static String JEREMASTER_UUID = "6c669475-3026-4603-b3e7-52c97681ad3a";
    public static String RYANLAND_UUID = "3134fb4d-a345-4c5e-9513-97c2c951223e";
    public static String OPTIONSTXT = "";
    public static String CLIENT_LANG = "unknown";
    public static String[] signText = {};//stores the text of the code sign corresponding to the currently open chest
    public static ModelBakery modelLoader;
    public static LimitedHashmap<String, BakedModel> modelCache = new LimitedHashmap<>(256);

    public static void info(String message) {
        LOGGER.info("[" + MOD_NAME + "] " + message);
    }

    public static void error(String message) {
        LOGGER.error("[" + MOD_NAME + "] " + message);
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

    @Override
    public void onInitialize() {
        optionsTxtPath = FabricLoader.getInstance().getGameDir().resolve("options.txt");
        try {
            OPTIONSTXT = FileUtil.readFile(optionsTxtPath.toString(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion().getFriendlyString();
        info("Initializing");
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
        PLAYER_NAME = MC.getUser().getName();
        PLAYER_UUID = MC.getUser().getUuid();

        // Initialize.
        CodeInitializer initializer = new CodeInitializer();
        initializer.add(new ConfigFile());
        initializer.add(new ConfigManager());
        initializer.add(new TemplateStorageHandler());
        initializer.add(new DFDiscordRPC());
        initializer.add(new ActionDump());
        initializer.add(new EventHandler());
        initializer.add(new State.Locater());
        initializer.add(new CommandHandler());

        // Initializes only if the given condition is met. (this case: config value)
        initializer.addIf(new SocketHandler(), Config.getBoolean("itemApi"));
        MC.tell(CosmeticHandler.INSTANCE::load);

        ClientTickEvents.START_CLIENT_TICK
                .register(client -> OtherEvents.TICK.invoker().tick(client));

        info("Initialized successfully!");
    }

}