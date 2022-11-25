plugins {
    `kotlin-dsl`
}

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