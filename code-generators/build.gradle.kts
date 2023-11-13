plugins {
    `kotlin-dsl`
    id("machine.java-conventions-library")
}

group = "org.machinemc"
version = "1.0.0"

dependencies {
    implementation(libs.jetbrains.kotlin.gradle)
    implementation(libs.google.gson)
    implementation(libs.asm)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
    compileJava {
        options.release.set(17)
    }
}