pluginManagement {
    repositories {
        mavenCentral()
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        gradlePluginPortal()
        jcenter() // TODO: remove when DiscordRPC dependency is gone
    }
}