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

    implementation(libs.google.guava)
    implementation(libs.google.gson)
    implementation(libs.netty.all)
    implementation(libs.jna)
    implementation(libs.jline)
    implementation(libs.mojang.brigadier)
    implementation(libs.machine.cogwheel.core)
    implementation(libs.machine.cogwheel.json)
    implementation(libs.machine.cogwheel.properties)
    implementation(libs.machine.landscape)
    implementation(libs.machine.nbt.core)
    implementation(libs.machine.nbt.parser)
    implementation(libs.machine.scriptive.core)
    implementation(libs.machine.scriptive.gson)
    implementation(libs.fastutil)

}