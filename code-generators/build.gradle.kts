plugins {
    `kotlin-dsl`
    id("machine.java-conventions-library")
}

dependencies {
    implementation(libs.jetbrains.kotlin.gradle)
    implementation(libs.google.gson)
    implementation(libs.asm)
}