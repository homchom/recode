plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
}

dependencies {
    fun pluginImplementation(plugin: Provider<PluginDependency>) {
        val dep = plugin.get()
        implementation(group = dep.pluginId, name = "${dep.pluginId}.gradle.plugin") {
            version {
                strictly(dep.version.strictVersion)
                require(dep.version.requiredVersion)
                prefer(dep.version.preferredVersion)
                for (version in dep.version.rejectedVersions) reject(version)
            }
        }
    }

    pluginImplementation(libs.plugins.kotlin.jvm)
    pluginImplementation(libs.plugins.fabric.loom)
}