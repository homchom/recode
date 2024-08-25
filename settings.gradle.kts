val modName: String by settings

rootProject.name = modName

include("mod")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

// https://github.com/FabricMC/fabric-loom/issues/1122 and https://github.com/gradle/gradle/issues/1370
buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("com.google.code.gson:gson:2.10.1")
    }
}