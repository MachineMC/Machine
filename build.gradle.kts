@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    application
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("machine.java-conventions-library")
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
    implementation(project(":application"))
}

application {
    mainClass.set("org.machinemc.application.MachineApplication")
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

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    standardOutput = System.out
    errorOutput = System.err
}