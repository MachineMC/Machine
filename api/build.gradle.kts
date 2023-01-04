@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("machine.java-conventions-library")
}

repositories {
    maven {
        url = uri("https://libraries.minecraft.net")
    }
}

dependencies {

    sequenceOf(
        "machine-materials",
        "machine-blockdata",
        "nbt"
    ).forEach {
        implementation(files("../libs/$it.jar"))
    }

    implementation(libs.jetbrains.annotations) // overrides default compileOnly

    implementation(libs.google.guava)

    implementation(libs.bundles.kyori.adventure)
    implementation(libs.mojang.brigadier)
}