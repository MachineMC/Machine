plugins {
    `kotlin-dsl`
}

group = "org.machinemc"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jetbrains.kotlin.gradle)
    compileOnly(files(libs::class.java.superclass.protectionDomain.codeSource.location))
}