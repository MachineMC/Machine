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

    sequenceOf(
        "machine-materials",
        "machine-blockdata",
    ).forEach {
        implementation(files("/libs/$it.jar"))
    }

    implementation(libs.machine.nbt)

    implementation(libs.jetbrains.annotations) // overrides default compileOnly

    implementation(libs.google.guava)

    implementation(libs.bundles.kyori.adventure)
    implementation(libs.mojang.brigadier)
}