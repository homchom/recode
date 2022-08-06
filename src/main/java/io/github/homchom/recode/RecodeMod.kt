@file:JvmName("Recode")

package io.github.homchom.recode

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.homchom.recode.feature.RenderingFeatureGroup
import io.github.homchom.recode.init.strongModule
import io.github.homchom.recode.mod.commands.CommandHandler
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.mod.config.internal.ConfigFile
import io.github.homchom.recode.mod.config.internal.ConfigInstruction
import io.github.homchom.recode.mod.config.internal.gson.ConfigSerializer
import io.github.homchom.recode.mod.config.internal.gson.types.*
import io.github.homchom.recode.mod.config.internal.gson.types.list.StringListSerializer
import io.github.homchom.recode.mod.config.structure.ConfigManager
import io.github.homchom.recode.mod.config.types.*
import io.github.homchom.recode.mod.config.types.list.StringListSetting
import io.github.homchom.recode.mod.events.LegacyEventHandler
import io.github.homchom.recode.mod.features.discordrpc.DFDiscordRPC
import io.github.homchom.recode.sys.hypercube.codeaction.ActionDump
import io.github.homchom.recode.sys.hypercube.templates.TemplateStorageHandler
import io.github.homchom.recode.sys.networking.websocket.SocketHandler
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private val Logger = LoggerFactory.getLogger(MOD_ID)

lateinit var modVersion: String
    private set

val RecodeMod = strongModule {
    // TODO: move feature groups to a config module
    depend(RenderingFeatureGroup)

    // On mod initialize
    onLoad {
        logInfo("Initializing...")

        modVersion = FabricLoader.getInstance().getModContainer(MOD_ID).get()
            .metadata.version.friendlyString

        System.setProperty("java.awt.headless", "false")

        ClientLifecycleEvents.CLIENT_STOPPING.register { disable() }

        LegacyRecode.onInitialize()

        logInfo("Initialized successfully!")
    }

    // On Minecraft close
    onDisable {
        logInfo("Closing...")

        // TODO: clean up
        try {
            ConfigFile.getInstance().save()
            TemplateStorageHandler.getInstance().save()
        } catch (err: Exception) {
            logError("Error")
            err.printStackTrace()
        }

        logInfo("Closed.")
    }
}

@Deprecated("Use top-level or Kotlin equivalents")
object LegacyRecode {
    @JvmField
    val RANDOM = Random()

    @JvmField
    val GSON: Gson = GsonBuilder()
        .registerTypeAdapter(ConfigInstruction::class.java, ConfigSerializer())
        .registerTypeAdapter(BooleanSetting::class.java, BooleanSerializer())
        .registerTypeAdapter(IntegerSetting::class.java, IntegerSerializer())
        .registerTypeAdapter(DoubleSetting::class.java, DoubleSerializer())
        .registerTypeAdapter(FloatSetting::class.java, FloatSerializer())
        .registerTypeAdapter(LongSetting::class.java, LongSerializer())
        .registerTypeAdapter(StringSetting::class.java, StringSerializer())
        .registerTypeAdapter(StringListSetting::class.java, StringListSerializer())
        .registerTypeAdapter(EnumSetting::class.java, EnumSerializer())
        .registerTypeAdapter(DynamicStringSetting::class.java, DynamicStringSerializer())
        .registerTypeAdapter(SoundSetting::class.java, SoundSerializer())
        .setPrettyPrinting()
        .create()

    @JvmField
    val MC: Minecraft = Minecraft.getInstance()

    @JvmField
    val executor: ExecutorService = Executors.newCachedThreadPool()

    // TODO: replace with something... better
    @JvmField
    var signText = arrayOf<String>() // stores the text of the code sign corresponding to the currently open chest

    fun onInitialize() {
        //System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,TLSv1.3");

        // Initialize legacy code
        val initializer = CodeInitializer()
        initializer.add(ConfigFile())
        initializer.add(ConfigManager())
        initializer.add(TemplateStorageHandler())
        initializer.add(DFDiscordRPC())
        initializer.add(ActionDump())
        initializer.add(LegacyEventHandler())
        initializer.add(CommandHandler())

        // Initializes only if the given condition is met. (this case: config value)
        initializer.addIf(SocketHandler(), Config.getBoolean("itemApi"))
    }

    @JvmStatic
    fun info(message: String) = logInfo("[$MOD_NAME] $message")

    @JvmStatic
    fun error(message: String) = logError("[$MOD_NAME] $message")
}

fun logInfo(message: String) = Logger.info("[$MOD_NAME] $message")

fun logError(message: String) = Logger.error("[$MOD_NAME] $message")