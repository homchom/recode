import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

object JvmVersions {
    val java get() = JavaVersion.VERSION_17
    val kotlin get() = JvmTarget.JVM_17
}

java {
    sourceCompatibility = JvmVersions.java
    targetCompatibility = JvmVersions.java
}

tasks.withType<JavaCompile>().configureEach {
    options.release = JvmVersions.java.majorVersion.toInt()
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmVersions.kotlin
        freeCompilerArgs = listOf(
            "-Xjvm-default=all"
        )
    }
}