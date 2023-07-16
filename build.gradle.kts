@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    application
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.johnrengelman.shadow)
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
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("")
        destinationDirectory.set(file("build/libs"))
    }
    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
    }
    distTar {
        dependsOn(shadowJar)
    }
    distZip {
        dependsOn(shadowJar)
    }
    startScripts {
        dependsOn(shadowJar)
    }
    shadowDistTar {
        dependsOn(jar)
    }
    shadowDistZip {
        dependsOn(jar)
    }
    startShadowScripts {
        dependsOn(jar)
    }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    standardOutput = System.out
    errorOutput = System.err
    args = listOf("smart-terminal")
}