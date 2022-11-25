@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    application
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.johnrengelman.shadow)
    id("machine.java-conventions-library")
    id("machine.generator-library")
}

group = "me.pesekjak"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        url = uri("https://jitpack.io")
        url = uri("https://libraries.minecraft.net")
    }
}

dependencies {

    sequenceOf(
        "Materials",
        "BlockData"
    ).forEach {
        implementation(files("libs/Machine$it.jar"))
    }

    implementation(libs.google.guava)
    implementation(libs.netty.buffer)
    implementation(libs.jna)
    implementation(libs.jline)

    implementation(libs.bundles.kyori.adventure)
    implementation(libs.bundles.hephaistos)
    implementation(libs.mojang.brigadier)
}

application {
    mainClass.set("me.pesekjak.machine.Machine")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
    }
    shadowJar {
        this.archiveClassifier.set("")
    }
}