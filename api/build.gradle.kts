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
        "Materials",
        "BlockData"
    ).forEach {
        implementation(files("../libs/Machine$it.jar"))
    }

    implementation(libs.jetbrains.annotations) // overrides default compileOnly

    implementation(libs.google.guava)

    implementation(libs.bundles.kyori.adventure)
    implementation(libs.bundles.hephaistos)
    implementation(libs.mojang.brigadier)
}