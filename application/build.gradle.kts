@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("machine.java-conventions-library")
    id("machine.generator-library")
}

group = "org.machinemc"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        url = uri("https://jitpack.io")
        url = uri("https://libraries.minecraft.net")
    }
}

dependencies {

    implementation(project(":api"))
    implementation(project(":server"))

    implementation(libs.google.gson)
    implementation(libs.jna)
    implementation(libs.jline)
    implementation(libs.mojang.brigadier)
    implementation(libs.machine.scriptive.core)

}