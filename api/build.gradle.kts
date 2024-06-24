plugins {
    id("java-library-convention")
}

repositories {
    maven("https://libraries.minecraft.net")
}

dependencies {
    implementation(libs.google.guava)

    implementation(libs.slf4j.api)

    implementation(libs.brigadier)

    implementation(libs.cogwheel.core)

    implementation(libs.nbt.core)

    implementation(libs.scriptive.core)

    implementation(libs.barebones.key)
    implementation(libs.barebones.profile)

    implementation(libs.fastutil)
}
