plugins {
    id("recode.fabric-conventions")

    id("com.modrinth.minotaur") version "2.+"
    id("com.github.johnrengelman.shadow") version "8.1.1"
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
val flkVersion: String by project

val requiredDependencyMods = dependencyModsOfType("required")
val optionalDependencyMods = dependencyModsOfType("optional")

base {
    archivesName = modName
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
    fun includeModImplementation(mod: Provider<MinimalExternalModuleDependency>) {
        modImplementation(mod)
        include(mod)
    }

    // minecraft
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())

    // fabric
    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.language.kotlin)

    // other required mods
    includeModImplementation(libs.adventure.platform.fabric)
    includeModImplementation(libs.libgui)
    includeModImplementation(libs.clothConfig)

    // optional mods
    modCompileOnly(libs.modmenu)
    modCompileOnly(libs.sodium)

    // Websocket TODO: clean this up
    shade(implementation("org.java-websocket:Java-WebSocket:1.5.3")!!)
    include(implementation("javax.websocket:javax.websocket-api:1.1")!!)
}

tasks.processResources {
    // these properties can be used in fabric_mod_json_template.txt in Groovy template syntax
    val exposedProperties = arrayOf(
        "modName" to modName,
        "version" to modVersion,
        "minecraftVersion" to minecraftVersion,
        "loaderVersion" to loaderVersion,
        "fabricVersion" to fabricVersion,
        "flkVersion" to flkVersion
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

tasks.jar {
    // disable jar (in favor of shadowJar)
    enabled = false
}

tasks.shadowJar {
    configurations = listOf(shade)
    from("LICENSE")

    // output shaded jar in the correct destination to be used by remapJar
    destinationDirectory = file("build/devlibs")
    archiveClassifier = "dev"

    // relocate
    isEnableRelocation = true
    relocationPrefix = "$mavenGroup.$modName.shaded"
}

tasks.remapJar {
    // use the shaded jar with remapJar
    inputFile.value(tasks.shadowJar.get().archiveFile)
}

tasks.modrinth.get().dependsOn(tasks.modrinthSyncBody)

modrinth {
    // DO NOT PUT THIS IN RECODE'S GRADLE.PROPERTIES. Your modrinth token should remain private to everyone.
    token = findProperty("privateModrinthToken")?.toString() ?: ""

    projectId = "recode"
    versionNumber = modVersionWithMeta

    val match = Regex("""-(?<phase>beta|alpha)\.""").find(modVersion)
    if (match == null) {
        versionName = modVersion
        versionType = "release"
    } else {
        val phase = match.groups["phase"]!!.value
        versionName = modVersion.replaceRange(match.range, " $phase ")
        versionType = phase
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
    syncBodyFrom = file("../README.md").readText()
    changelog = file("../CHANGELOG.md").readText()
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