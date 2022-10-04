plugins {
    java
    application
    id("com.github.johnrengelman.shadow")
}

group = "me.pesekjak"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(uri("https://jitpack.io"))
}

dependencies {

    // JetBrains Annotations
    implementation("org.jetbrains:annotations:20.1.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.24")
    testCompileOnly("org.projectlombok:lombok:1.18.24")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    // Gson
    implementation("com.google.code.gson:gson:2.7")

    // ASM
    implementation("org.ow2.asm:asm:9.2")

}

application {
    mainClass.set("me.pesekjak.machine.codegen.Generators")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
    }

}