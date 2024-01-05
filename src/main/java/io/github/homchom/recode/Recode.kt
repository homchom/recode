package io.github.homchom.recode

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.homchom.recode.feature.automation.AutoCommands
import io.github.homchom.recode.feature.visual.FBuiltInResourcePacks
import io.github.homchom.recode.feature.visual.FCodeSearch
import io.github.homchom.recode.feature.visual.FSignRenderDistance
import io.github.homchom.recode.game.QuitGameEvent
import io.github.homchom.recode.hypercube.JoinDFDetector
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
import io.github.homchom.recode.sys.hypercube.codeaction.ActionDump
import io.github.homchom.recode.sys.hypercube.templates.TemplateStorageHandler
import io.github.homchom.recode.sys.networking.websocket.SocketHandler
import io.github.homchom.recode.ui.showRecodeMessage
import io.github.homchom.recode.ui.text.literalText
import io.github.homchom.recode.ui.text.translatedText
import io.github.homchom.recode.util.regex.groupValue
import io.github.homchom.recode.util.regex.regex
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.metadata.ModMetadata
import net.fabricmc.loader.api.metadata.ModOrigin
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Recode : ModContainer {
    /**
     * A prettified version of the mod's version. For example, "0.1.2-beta.3" is prettified to "0.1.2 beta 3".
     */
    // TODO: i assume there isn't a way to share this code with the buildscript?
    val version: String by lazy {
        val raw = metadata.version.friendlyString
        val preReleaseMatch = regex {
            str('-')
            val phase by group { str("alpha"); or; str("beta") }
            str('.')
        }.find(raw)
        if (preReleaseMatch == null) raw else {
            val phase = preReleaseMatch.groupValue("phase")
            raw.replaceRange(preReleaseMatch.range, " $phase ")
        }
    }

    private val container by lazy { FabricLoader.getInstance().getModContainer(MOD_ID).get() }

    private var isInitialized = false

    private val power = Power(onEnable = { registerTopLevelListeners() })

    // initialize features TODO: replace with FeatureGroups during config refactor
    init {
        // Automation
        AutoCommands

        // Chat and Social

        // Visual
        FBuiltInResourcePacks
        FCodeSearch
        FSignRenderDistance
    }

    /**
     * Initializes the mod. This should only be called once, from an entrypoint.
     */
    fun initialize() {
        check(!isInitialized) { "$MOD_NAME has already been initialized" }
        logInfo("initializing...")

        LegacyRecode.onInitialize()

        runBlocking { power.up() }

        isInitialized = true
        logInfo("initialized successfully")
    }

    // register globally active listeners that aren't feature-related
    private fun Power.registerTopLevelListeners() {
        // handle close
        QuitGameEvent.listenEach { close() }

        // show mod usage messages
        JoinDFDetector.listenEach {
            showRecodeMessage(translatedText(
                "recode.using",
                args = arrayOf(literalText(version))
            ))
        }
    }

    private fun close() {
        logInfo("closing...")

        ConfigFile.getInstance().save()
        TemplateStorageHandler.getInstance().save()

        logInfo("closed successfully")
    }

    override fun getMetadata(): ModMetadata = container.metadata
    override fun getRootPaths(): List<Path> = container.rootPaths
    override fun getOrigin(): ModOrigin = container.origin
    override fun getContainingMod(): Optional<ModContainer> = container.containingMod
    override fun getContainedMods(): Collection<ModContainer> = container.containedMods

    @Deprecated("Use getRootPaths instead")
    @Suppress("Deprecation")
    override fun getRootPath(): Path = container.rootPath

    @Deprecated("Use getRootPaths instead")
    @Suppress("Deprecation")
    override fun getPath(file: String?): Path = container.getPath(file)
}

@Deprecated("Use top-level or Kotlin equivalents")
object LegacyRecode {
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
    val executor: ExecutorService = Executors.newCachedThreadPool()

    // TODO: replace with something... better
    @JvmField
    var signText = arrayOf<String>() // stores the text of the code sign corresponding to the currently open chest

    fun onInitialize() {
        //System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,TLSv1.3");

        // Initialize legacy code
        val initializer = CodeInitializer()
        initializer.add(ConfigFile())
        initializer.add(ConfigManager.getInstance())
        initializer.add(TemplateStorageHandler())
        initializer.add(ActionDump())
        initializer.add(LegacyEventHandler())

        // Initializes only if the given condition is met. (this case: config value)
        initializer.addIf(SocketHandler(), Config.getBoolean("itemApi"))

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, registryAccess ->
            CommandHandler.load(dispatcher, registryAccess)
        }
    }
}