plugins {
    // removing this causes issues, but we need to remove it to free up the kotlin plugin on the buildscript
    //alias(libs.plugins.fabric.loom) apply false
}

repositories {
    mavenCentral()
}