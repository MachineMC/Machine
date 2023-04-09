@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    application
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

    sequenceOf(
        "machine-materials",
        "machine-blockdata",
    ).forEach {
        implementation(files("libs/$it.jar"))
    }

    implementation(libs.google.guava)
    implementation(libs.google.gson)
    implementation(libs.netty.buffer)
    implementation(libs.jna)
    implementation(libs.jline)
    implementation(libs.mojang.brigadier)
    implementation(libs.machine.nbt)
    implementation(libs.machine.landscape)
    implementation(libs.machine.scriptive)
}

application {
    mainClass.set("org.machinemc.server.Machine")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
    }
    buildAll {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
    }
}