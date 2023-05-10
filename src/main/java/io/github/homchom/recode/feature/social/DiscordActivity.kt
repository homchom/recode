package io.github.homchom.recode.feature.social

/*import de.jcm.discordgamesdk.Core
import de.jcm.discordgamesdk.CreateParams
import de.jcm.discordgamesdk.activity.Activity
import io.github.homchom.recode.DISCORD_APP_ID
import io.github.homchom.recode.MOD_NAME
import io.github.homchom.recode.feature.feature
import io.github.homchom.recode.feature.social.DiscordActivityTimeMeasurement.*
import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.lifecycle.ModuleDetail
import io.github.homchom.recode.lifecycle.emptyModuleList
import io.github.homchom.recode.logError
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.mod.config.types.IConfigEnum
import io.github.homchom.recode.server.*
import io.github.homchom.recode.trimmedModVersion
import io.github.homchom.recode.util.capitalize
import io.github.homchom.recode.util.platform
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import oshi.PlatformEnum
import java.net.URL
import java.nio.file.Files
import java.nio.file.Files.createTempDirectory
import java.nio.file.Path
import java.time.Instant
import java.util.zip.ZipInputStream
import kotlin.io.path.deleteIfExists
import kotlin.io.path.div

val FDiscordIntegration = feature("Discord Integration"/*, DiscordIntegrationDetail()*/) {
    onEnable {
        println("jna.library.path = ${System.getProperty("jna.library.path")}")
    }
}

private class DiscordIntegrationDetail : ModuleDetail {
    val activityWatermark get() = "$SERVER_ADDRESS | $MOD_NAME $trimmedModVersion"

    private var libraryPath: Path? = null

    private val startTimes = mutableMapOf<DiscordActivityTimeMeasurement, Instant>()

    init {
        setStartTime(IN_GAME)
    }

    fun setStartTime(measurement: DiscordActivityTimeMeasurement) {
        startTimes[measurement] = Instant.now()
    }

    fun getConfiguredStartTime() =
        startTimes[Config.getEnum("discordActivityTimeMeasurement", DiscordActivityTimeMeasurement::class.java)]

    override fun ExposedModule.onLoad() {}

    override fun ExposedModule.onEnable() {
        launch {
            libraryPath = downloadLibrary()
            val library = libraryPath
            if (library == null) {
                logError("$MOD_NAME's Discord integration does not recognize your operating system " +
                        "and will not be enabled.", true)
            } else {
                Core.init(library.toFile())

                val mutex = Mutex()
                CreateParams().use { params ->
                    params.clientID = DISCORD_APP_ID
                    params.flags = CreateParams.getDefaultFlags()
                    Core(params).use { core ->
                        makeActivity(core, mutex)

                        while (isActive) {
                            mutex.withLock { core.runCallbacks() }
                            delay(16) // arbitrary delay to reduce CPU usage
                        }
                    }
                }
            }
        }
    }

    override fun ExposedModule.onDisable() {
        libraryPath?.deleteIfExists()
    }

    override fun children() = emptyModuleList()

    private fun downloadLibrary(): Path? {
        val libraryName = "discord_game_sdk"
        val suffix = when (platform) {
            PlatformEnum.WINDOWS -> ".dll"
            PlatformEnum.MACOS -> ".dylib"
            PlatformEnum.LINUX -> ".so"
            else -> return null
        }
        val arch = System.getProperty("os.arch").lowercase().let {
            if (it == "amd64") "x86_64" else it
        }

        val libraryPath = "lib/$arch/$libraryName$suffix"

        val downloadUrl = URL("https://dl-game-sdk.discordapp.net/3.2.1/discord_game_sdk.zip")
        val connection = downloadUrl.openConnection()
        connection.setRequestProperty("User-Agent", MOD_NAME)

        val zipInput = ZipInputStream(connection.getInputStream())
        val zipSequence = with(zipInput) {
            generateSequence(nextEntry) {
                closeEntry()
                nextEntry
            }
        }

        for (entry in zipSequence) {
            if (entry.name == libraryPath) {
                val tempDir = createTempDirectory("java-$libraryName")
                val temp = tempDir / (libraryName + suffix)
                Files.copy(zipInput, temp)

                zipInput.close()
                return temp
            }

            zipInput.closeEntry()
        }

        zipInput.close()
        return null
    }

    private fun ExposedModule.makeActivity(core: Core, mutex: Mutex) = Activity().use { activity ->
        // TODO: statically typed config variables via delegated parameters?
        val vars = mutableMapOf<String, String>()

        fun dynamic(key: String) = Config.getDynamicString(key, vars).takeIf { it.isNotEmpty() }

        JoinDFDetector.listenEach { setStartTime(ON_SERVER) }

        DFStateDetectors.replayAndListenEach listener@{ state ->
            if (!Config.getBoolean("discordRPC")) return@listener
            val assets = activity.assets()

            if (state != null) vars["node.id"] = state.node.displayName

            when (state) {
                is SpawnState -> {
                    activity.details = dynamic("discordRPCSpawnDetails")
                    activity.state = dynamic("discordRPCSpawnState")

                    // TODO: readd
                    /*if (Config.getBoolean("discordRPCShowSession") && state.isInSession()) {
                        activity.setSmallImage("supportsession", "In Support Session");
                    } else {
                        activity.setSmallImage(null, null);
                    }*/

                    assets.smallImage = null
                    assets.smallText = null
                    assets.largeImage = "diamondfirelogo"
                    assets.largeText = activityWatermark
                }
                is PlayState -> {
                    vars["plot.name"] = state.plot.name
                    vars["plot.id"] = state.plot.id.toString()
                    vars["plot.status"] = state.status.orEmpty()
                    activity.details = dynamic("discordRPCPlotDetails") //+ " "
                    activity.state = dynamic("discordRPCPlotState")
                    if (Config.getBoolean("discordRPCShowPlotMode")) {
                        assets.smallImage = "mode" + state.mode.id
                        assets.smallText = state.mode.descriptor.capitalize()
                    }

                    // TODO: readd
                    /*if (Config.getBoolean("discordRPCShowSession") && state.isInSession()) {
                        activity.setSmallImage(
                            "supportsession",
                            "In Support Session (" + playState.getMode().getDescriptor() + ")"
                        );
                    }*/

                    assets.largeImage = "diamondfirelogo"
                    assets.largeText = state.status ?: activityWatermark

                    val oldState = DFStateDetectors.prevResult
                    if (oldState is PlayState) {
                        if (state.plot.id != oldState.plot.id) setStartTime(ON_PLOT)
                        if (state.mode != oldState.mode) setStartTime(IN_MODE)
                    }

                    val showTime = Config.getBoolean("discordRPCShowElapsed")
                    activity.timestamps().start = if (showTime) getConfiguredStartTime() else null

                    mutex.withLock { core.activityManager().updateActivity(activity) }
                }
                else -> mutex.withLock { core.activityManager().clearActivity() }
            }
        }
    }
}

enum class DiscordActivityTimeMeasurement : IConfigEnum {
    IN_GAME, ON_SERVER, ON_PLOT, IN_MODE;

    override fun getKey() = "discordActivityTimeMeasurement"
}*/