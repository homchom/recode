import Build_gradle.DependencyMod
import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    id("fabric-loom") version "0.12-SNAPSHOT"
    id("com.modrinth.minotaur") version "2.+"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val modName: String by project

val modVersion: String by project
val minecraftVersion: String by project
val modVersionWithMeta = "$modVersion+$minecraftVersion"
version = modVersionWithMeta

val mavenGroup: String by project
group = mavenGroup

val fabricVersion: String by project

val requiredDependencyMods = dependencyModsOfType("required")
val optionalDependencyMods = dependencyModsOfType("optional")

base {
    archivesName.set(modName)
}

repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter { includeGroup("maven.modrinth") }
    }

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
    // exclude slf4j because it is already provided by Minecraft TODO: can this workaround be removed now?
    exclude(group = "org.slf4j")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())

    val loaderDevVersion: String by project
    modImplementation("net.fabricmc:fabric-loader:$loaderDevVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

    shadeApi(kotlin("stdlib", "1.8.21"))
    shadeApi("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    // Declare mod dependencies listed in gradle.properties
    for (mod in requiredDependencyMods) includeModImpl("${mod.artifact}:${mod.version}")
    for (mod in optionalDependencyMods) modCompileOnly("${mod.artifact}:${mod.version}")

    // Websocket TODO: clean this up
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
            // Compile Kotlin interfaces with Java 8 default methods
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }

    processResources {
        val loaderModVersion: String by project
        // These properties can be used in fabric_mod_json_template.txt in Groovy template syntax
        val exposedProperties = arrayOf(
            "modName" to modName,
            "version" to version,
            "minecraftVersion" to minecraftVersion,
            "loaderVersion" to loaderModVersion,
            "fabricVersion" to fabricVersion
        )

        inputs.properties(*exposedProperties)
        inputs.properties(project.properties.filterKeys { it.startsWith("required.") })

        // Evaluate fabric_mod_json_template.txt as a Groovy template
        filesMatching("fabric_mod_json_template.txt") {
            val metadataRegex = Regex("""\+[\d.]+?$""")
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
        // repackage shaded dependencies
        prefix = "$mavenGroup.recode.shaded"
    }

    shadowJar {
        dependsOn(relocate)
        configurations = listOf(shade)
        // output shaded jar in the correct destination to be used by remapJar
        destinationDirectory.set(file("build/devlibs"))
        archiveClassifier.set("dev")

        from("LICENSE")
    }

    remapJar {
        // use the shaded jar with remapJar, since jar is disabled
        inputFile.value(shadowJar.get().archiveFile)
    }
}

tasks.modrinth.get().dependsOn(tasks.modrinthSyncBody.get())

modrinth {
    // DO NOT PUT THIS IN RECODE'S GRADLE.PROPERTIES. Your modrinth token should remain private to everyone.
    token.set(findProperty("privateModrinthToken")?.toString() ?: "")

    projectId.set("recode")
    versionNumber.set(modVersionWithMeta)

    val match = Regex("""-(beta|alpha)(\.)?""").find(modVersion)
    if (match == null) {
        versionName.set(modVersion)
        versionType.set("release")
    } else {
        val type = match.groupValues[1]
        val replacement = if (match.groups.size == 3) " $type " else " $type"
        versionName.set(modVersion.replaceRange(match.range, replacement))
        versionType.set(type)
    }

    uploadFile.set(tasks.remapJar.get())
    gameVersions.addAll(minecraftVersion)
    dependencies {
        val fabricModrinthVersion: String by project
        required.version(fabricModrinthVersion)
    }

    // TODO: use something other than readText?
    syncBodyFrom.set(file("README.md").readText())
    changelog.set(file("CHANGELOG.md").readText())
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

/**
 * @return The list of [DependencyMod] values matching [type] in gradle.properties.
 */
fun dependencyModsOfType(type: String) = properties.mapNotNull { (key, value) ->
    Regex("""$type\.([a-z][a-z0-9-_]{1,63})\.artifact""").matchEntire(key)?.let { match ->
        val id = match.groupValues[1]
        DependencyMod(
            id,
            value,
            project.properties["$type.$id.version"],
            project.properties.getOrDefault("$type.$id.versionSpec", "^")
        )
    }
}