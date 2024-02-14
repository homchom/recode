import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("fabric-loom") version "1.5-SNAPSHOT"
    id("com.modrinth.minotaur") version "2.+"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val modName: String by project
val modVersion: String by project
val minecraftVersion: String by project
val modVersionWithMeta get() = "$modVersion+$minecraftVersion"
version = "$modVersionWithMeta-LATEST"

val mavenGroup: String by project
group = mavenGroup

val fabricVersion: String by project
val loaderVersion: String by project

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
    maven {
        url = uri("https://jitpack.io")
    }
    mavenCentral()
}

val shade: Configuration by configurations.creating {
    isCanBeResolved = true
    // exclude slf4j because it is already provided by Minecraft TODO: can this workaround be removed now?
    exclude(group = "org.slf4j")
}

dependencies {
    // minecraft
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())

    // fabric
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

    // kotlin
    shade(api(kotlin("stdlib", "1.9.0"))!!)
    shade(api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")!!)

    // mod dependencies listed in gradle.properties
    for (mod in requiredDependencyMods) {
        include(modImplementation("${mod.artifact}:${mod.version}")!!)
    }
    for (mod in optionalDependencyMods) {
        modCompileOnly("${mod.artifact}:${mod.version}")
    }

    // Websocket TODO: clean this up
    shade(implementation("org.java-websocket:Java-WebSocket:1.5.3")!!)
    include(implementation("javax.websocket:javax.websocket-api:1.1")!!)
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
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.set(listOf(
                "-Xjvm-default=all",
            ))
        }
    }

    processResources {
        // these properties can be used in fabric_mod_json_template.txt in Groovy template syntax
        val exposedProperties = arrayOf(
            "modName" to modName,
            "version" to modVersion,
            "minecraftVersion" to minecraftVersion,
            "loaderVersion" to loaderVersion,
            "fabricVersion" to fabricVersion
        )

        inputs.properties(*exposedProperties)
        inputs.properties(project.properties.filterKeys { it.startsWith("required.") })

        // evaluate fabric_mod_json_template.txt as a Groovy template
        filesMatching("fabric_mod_json_template.txt") {
            val metadataRegex = Regex("""\+.+$""")
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
        // disable jar (in favor of shadowJar)
        enabled = false
    }

    val relocate by registering(ConfigureShadowRelocation::class) {
        // repackage shaded dependencies
        target = shadowJar.get()
        prefix = "$mavenGroup.recode.shaded"
    }

    shadowJar {
        dependsOn(relocate.get())
        configurations = listOf(shade)
        // output shaded jar in the correct destination to be used by remapJar
        destinationDirectory.set(file("build/devlibs"))
        archiveClassifier.set("dev")

        from("LICENSE")
    }

    remapJar {
        // use the shaded jar with remapJar
        inputFile.value(shadowJar.get().archiveFile)
    }
}

tasks.modrinth.get().dependsOn(tasks.modrinthSyncBody)

modrinth {
    // DO NOT PUT THIS IN RECODE'S GRADLE.PROPERTIES. Your modrinth token should remain private to everyone.
    token.set(findProperty("privateModrinthToken")?.toString() ?: "")

    projectId.set("recode")
    versionNumber.set(modVersionWithMeta)

    val match = Regex("""-(?<phase>beta|alpha)\.""").find(modVersion)
    if (match == null) {
        versionName.set(modVersion)
        versionType.set("release")
    } else {
        val phase = match.groups["phase"]!!.value
        versionName.set(modVersion.replaceRange(match.range, " $phase "))
        versionType.set(phase)
    }

    // remove "LATEST" classifiers when uploading to modrinth
    uploadFile.set(tasks.remapJar.map { task ->
        val jarFile = task.archiveFile.get().asFile
        val newPath = jarFile.path.replace("-LATEST", "")
        jarFile.renameTo(File(newPath))
        newPath
    })

    gameVersions.addAll(minecraftVersion)
    dependencies {
        required.version("fabric-api", fabricVersion)
    }

    // TODO: use something other than readText?
    syncBodyFrom.set(file("README.md").readText())
    changelog.set(file("CHANGELOG.md").readText())
}

data class DependencyMod(
    val id: String,
    val artifact: String,
    val version: String,
    val versionSpec: String
) {
    constructor(id: String, artifact: Any?, version: Any?, versionSpec: Any?) :
            this(id, artifact.toString(), version.toString(), versionSpec.toString())
}

/**
 * @return The list of [DependencyMod] values matching [type] in gradle.properties.
 */
fun dependencyModsOfType(type: String): List<DependencyMod> {
    val regex = Regex("""$type\.([^\.]+)\.artifact""")
    return properties.mapNotNull { (key, value) ->
        regex.matchEntire(key)?.let { match ->
            val id = match.groupValues[1]
            val version = project.properties["$type.$id.version"]
            val versionSpec = project.properties.getOrDefault("$type.$id.versionSpec", "^")
            DependencyMod(id, value, version, versionSpec)
        }
    }
}