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

    implementation(libs.machine.cogwheel.core)
    implementation(libs.machine.nbt.core)
    implementation(libs.machine.scriptive.core)
    implementation(libs.machine.scriptive.gson)
    implementation(libs.jetbrains.annotations) // overrides default compileOnly
    implementation(libs.google.guava)
    implementation(libs.google.gson)
    implementation(libs.netty.all)
    implementation(libs.mojang.brigadier)
    implementation(libs.fastutil)

}