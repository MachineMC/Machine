import org.machinemc.paklet.plugin.PakletPlugin

plugins {
    application
    alias(libs.plugins.shadow)
    id("java-library-convention")
}

buildscript {
    repositories {
        maven("https://repo.machinemc.org/releases")
    }
    dependencies {
        val paklet = libs.plugins.paklet.plugin.get()
        classpath("org.machinemc:${paklet.pluginId}:${paklet.version}")
    }
}

apply<PakletPlugin>()

repositories {
    maven("https://libraries.minecraft.net")
}

dependencies {
    implementation(project(":api"))

    implementation(libs.asm)

    implementation(libs.google.guava)
    implementation(libs.google.gson)

    implementation(libs.netty)

    implementation(libs.jna)
    implementation(libs.jline)
    implementation(libs.slf4j.jultoslf4j)
    implementation(libs.logback.classic)

    implementation(libs.brigadier)

    implementation(libs.paklet.api)
    implementation(libs.paklet.core)
    annotationProcessor(libs.paklet.processor)

    implementation(libs.cogwheel.core)
    implementation(libs.cogwheel.json)
    implementation(libs.cogwheel.properties)
    implementation(libs.cogwheel.yaml)

    implementation(libs.landscape)

    implementation(libs.nbt.core)
    implementation(libs.nbt.parser)

    implementation(libs.scriptive.core)
    implementation(libs.scriptive.gson)
    implementation(libs.scriptive.nbt)

    implementation(libs.barebones.key)
    implementation(libs.barebones.profile)

    implementation(libs.fastutil)
}

application {
    mainClass.set("org.machinemc.Machine")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    withType<Jar> {
        archiveBaseName = "Machine"
    }
    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
    }
    shadowJar {
        archiveClassifier = ""
        mergeServiceFiles()
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
