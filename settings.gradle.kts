val modName: String by settings

rootProject.name = modName

include("mod")

pluginManagement {
    repositories {
        mavenCentral()
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        gradlePluginPortal()
    }
}