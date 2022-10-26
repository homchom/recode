import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// TODO: document this file

plugins {
    kotlin("jvm") version "1.7.0"
    id("fabric-loom") version "0.12-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val modName: String by project

val modVersion: String by project
val minecraftVersion: String by project
version = "$modVersion+$minecraftVersion"

val mavenGroup: String by project
group = mavenGroup

val loaderVersion: String by project
val fabricVersion: String by project

val requiredDependencyMods = dependencyModsOfType("required")
val optionalDependencyMods = dependencyModsOfType("optional")

base {
    archivesName.set(modName)
}

repositories {
    jcenter()
    maven {
        name = "CottonMC"
        url = uri("https://server.bbkr.space/artifactory/libs-release")
    }
    maven {
        url = uri("https://maven.shedaniel.me/")
    }
    maven {
        url = uri("https://maven.terraformersmc.com/")
    }
    mavenCentral()
}

val shade: Configuration by configurations.creating {
    isCanBeResolved = true
    exclude(group = "org.slf4j")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

    shadeApi(kotlin("stdlib", "1.7.0"))
    shadeApi("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    for (mod in requiredDependencyMods) includeModImpl("${mod.artifact}:${mod.version}")
    for (mod in optionalDependencyMods) modCompileOnly("${mod.artifact}:${mod.version}")

    // discord rpc
    shadeImpl("com.jagrosh:DiscordIPC:0.4")

    // websocket TODO: clean this up
    shadeImpl("org.java-websocket:Java-WebSocket:1.5.3")
    includeImpl("javax.websocket:javax.websocket-api:1.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present. If you remove this line, sources will not be generated.
	withSourcesJar()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }

    processResources {
        val exposedProperties = arrayOf(
            "modName" to modName,
            "version" to version,
            "minecraftVersion" to minecraftVersion,
            "loaderVersion" to loaderVersion,
            "fabricVersion" to fabricVersion
        )

        inputs.properties(*exposedProperties)
        inputs.properties(project.properties.filterKeys { it.startsWith("required.") })

        filesMatching("fabric_mod_json_template.txt") {
            val metadataRegex = Regex("""\+[\d\.]+$""")
            expand(
                *exposedProperties,
                "metadataRegex" to metadataRegex.toPattern(),
                "dependencyMods" to requiredDependencyMods.joinToString(", ") { mod ->
                    val version = mod.version.replace(metadataRegex, "")
                    "\"${mod.id}\": \"${mod.versionSpec}$version\""
                }
            )
        }
        rename("fabric_mod_json_template.txt", "fabric.mod.json")
    }

    jar {
        enabled = false
    }

    val relocate by registering(ConfigureShadowRelocation::class) {
        target = shadowJar.get()
        prefix = "$mavenGroup.recode.shaded"
    }

    shadowJar {
        dependsOn(relocate)
        configurations = listOf(shade)
        destinationDirectory.set(file("build/devlibs"))
        archiveClassifier.set("dev")

        from("LICENSE")
    }

    remapJar {
        inputFile.value(shadowJar.get().archiveFile)
    }
}

typealias DependencyConfig = Action<ExternalModuleDependency>

fun DependencyHandlerScope.shadeImpl(notation: Any) {
    implementation(notation)
    shade(notation)
}

fun DependencyHandlerScope.shadeApi(notation: Any) {
    api(notation)
    shade(notation)
}

fun DependencyHandlerScope.includeImpl(notation: Any) {
    implementation(notation)
    include(notation)
}

fun DependencyHandlerScope.includeApi(notation: Any) {
    api(notation)
    include(notation)
}

fun DependencyHandlerScope.includeModImpl(notation: Any) {
    modImplementation(notation)
    include(notation)
}

fun DependencyHandlerScope.includeModApi(notation: Any) {
    modApi(notation)
    include(notation)
}

data class DependencyMod(
    val id: String,
    val artifact: String,
    val version: String,
    val versionSpec: String
) {
    constructor(id: String, artifact: Any?, version: Any?, versionSpec: Any?) :
            this(id, artifact.toString(), version.toString(), versionSpec.toString())

    val versionKey get() = ""
}

fun dependencyModsOfType(type: String) = properties.mapNotNull { (key, value) ->
    Regex("""$type\.([a-z][a-z0-9-_]{1,63})\.artifact""").matchEntire(key)?.let { match ->
        val id = match.groupValues[1]
        DependencyMod(
            id,
            value,
            project.properties["$type.$id.version"],
            project.properties["$type.$id.versionSpec"]
        )
    }
}