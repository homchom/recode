package io.github.homchom.recode

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.homchom.recode.feature.RenderingFeatureGroup
import io.github.homchom.recode.init.init
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
import io.github.homchom.recode.sys.networking.DFState.Locater
import io.github.homchom.recode.sys.networking.websocket.SocketHandler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Recode : ModInitializer {
    // TODO: move feature groups to a config module
    private val rootModules = listOf(
       RenderingFeatureGroup()
    )

    private val logger = LoggerFactory.getLogger(MOD_ID)

    @JvmField
    @Deprecated("Use kotlin.random")
    val RANDOM = Random()

    @JvmField
    @Deprecated("Use kotlinx.serialization")
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
    @Deprecated("Use 'mc' top-level property")
    val MC: Minecraft = Minecraft.getInstance()

    @JvmField
    val EXECUTOR: ExecutorService = Executors.newCachedThreadPool()

    @JvmStatic
    lateinit var version: String
        private set

    // TODO: replace with Permission class
    const val JEREMASTER_UUID = "6c669475-3026-4603-b3e7-52c97681ad3a"
    const val RYANLAND_UUID = "3134fb4d-a345-4c5e-9513-97c2c951223e"

    // TODO: replace with something... better
    @JvmField
    var signText = arrayOf<String>() // stores the text of the code sign corresponding to the currently open chest

    override fun onInitialize() {
        info("Initializing...")

        version = FabricLoader.getInstance().getModContainer(MOD_ID).get()
            .metadata.version.friendlyString

        System.setProperty("java.awt.headless", "false")

        ClientLifecycleEvents.CLIENT_STOPPING.register(::onClose)

        //System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,TLSv1.3");

        // Initialize root modules
        for (module in rootModules) module.init()

        // Initialize legacy code
        val initializer = CodeInitializer()
        initializer.add(ConfigFile())
        initializer.add(ConfigManager())
        initializer.add(TemplateStorageHandler())
        initializer.add(DFDiscordRPC())
        initializer.add(ActionDump())
        initializer.add(LegacyEventHandler())
        initializer.add(Locater())
        initializer.add(CommandHandler())

        // Initializes only if the given condition is met. (this case: config value)
        initializer.addIf(SocketHandler(), Config.getBoolean("itemApi"))

        info("Initialized successfully!")
    }

    private fun onClose(mc: Minecraft) {
        info("Closing...")
        try {
            ConfigFile.getInstance().save()
            TemplateStorageHandler.getInstance().save()
        } catch (err: Exception) {
            error("Error")
            err.printStackTrace()
        }
        info("Closed.")
    }

    @JvmStatic
    fun info(message: String) = logger.info("[$MOD_NAME] $message")

    @JvmStatic
    fun error(message: String) = logger.error("[$MOD_NAME] $message")
}