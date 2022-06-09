import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.kotlin.dsl.DependencyHandlerScope

plugins {
    id("fabric-loom") version "0.11-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.6.21"
}

val modVersion: String by project
version = modVersion
val mavenGroup: String by project
group = mavenGroup

base {
    val archivesBaseName: String by project
    archivesName.set(archivesBaseName)
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
    val minecraftVersion: String by project
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())

    val loaderVersion: String by project
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    val fabricVersion: String by project
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    val kotlinVersion: String by project
    includeModImpl("net.fabricmc:fabric-language-kotlin:1.7.4+kotlin.$kotlinVersion")

    // https://github.com/CottonMC/LibGui/releases
    includeModImpl("io.github.cottonmc:LibGui:5.4.0+1.18.2")

    modCompileOnly("com.terraformersmc:modmenu:3.2.1")
    includeModImpl("me.shedaniel.cloth:cloth-config-fabric:6.2.62")

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
	// if it is present.
	// If you remove this line, sources will not be generated.
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
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
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
    }

    remapJar {
        inputFile.value(shadowJar.get().archiveFile)
    }
}

typealias DependencyConfig = Action<ExternalModuleDependency>

fun DependencyHandlerScope.shadeImpl(notation: String) {
    implementation(notation)
    shade(notation)
}

fun DependencyHandlerScope.shadeImpl(notation: String, config: DependencyConfig) {
    implementation(notation, config)
    shade(notation)
}

fun DependencyHandlerScope.includeImpl(notation: String) {
    implementation(notation)
    include(notation)
}

fun DependencyHandlerScope.includeImpl(notation: String, config: DependencyConfig) {
    implementation(notation, config)
    include(notation, config)
}

fun DependencyHandlerScope.includeModImpl(notation: String) {
    modImplementation(notation)
    include(notation)
}

fun DependencyHandlerScope.includeModImpl(notation: String, config: DependencyConfig) {
    modImplementation(notation, config)
    include(notation, config)
}