plugins {
    java
    application
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "me.pesekjak"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {

    // JetBrains Annotations
    implementation("org.jetbrains:annotations:20.1.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.24")
    testCompileOnly("org.projectlombok:lombok:1.18.24")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    // Adventure
    implementation("net.kyori:adventure-api:4.11.0")
    implementation("net.kyori:adventure-text-serializer-gson:4.11.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.11.0")
    implementation("net.kyori:adventure-text-serializer-plain:4.11.0")

    // Hephaistos
    implementation("io.github.jglrxavpok.hephaistos:common:2.5.1")
    implementation("io.github.jglrxavpok.hephaistos:gson:2.5.1")

    // JSON Simple
    implementation("com.googlecode.json-simple:json-simple:1.1.1")

    // Netty Buffers
    implementation("io.netty:netty-buffer:4.1.80.Final")

    // JNA
    implementation("net.java.dev.jna:jna-platform:5.12.1")

    // Guava
    implementation("com.google.guava:guava:11.0.2")

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

}