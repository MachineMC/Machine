rootProject.name = "Machine"

pluginManagement {
    includeBuild("build-logic")
    // includeBuild("code-generators")
}

include("api")
include("server")

dependencyResolutionManagement {
    versionCatalogs {

        create("libs") {

            //
            // Dependencies
            //

            val jetbrainsAnnotations: String by settings
            library("jetbrains-annotations", "org.jetbrains:annotations:$jetbrainsAnnotations")

            val junit: String by settings
            library("junit-api", "org.junit.jupiter:junit-jupiter-api:$junit")
            library("junit-engine", "org.junit.jupiter:junit-jupiter-engine:$junit")
            library("junit-params", "org.junit.jupiter:junit-jupiter-params:$junit")

            val lombok: String by settings
            library("lombok", "org.projectlombok:lombok:$lombok")

            val slf4j: String by settings
            library("slf4j-api", "org.slf4j:slf4j-api:$slf4j")
            library("slf4j-jultoslf4j", "org.slf4j:jul-to-slf4j:$slf4j")

            val logback: String by settings
            library("logback-classic", "ch.qos.logback:logback-classic:$logback")

            val googleGuava: String by settings
            library("google-guava", "com.google.guava:guava:$googleGuava")

            val googleGson: String by settings
            library("google-gson", "com.google.code.gson:gson:$googleGson")

            val asm: String by settings
            library("asm", "org.ow2.asm:asm:$asm")

            val netty: String by settings
            library("netty", "io.netty:netty-all:$netty")

            val jna: String by settings
            library("jna", "net.java.dev.jna:jna-platform:$jna")

            val fastutil: String by settings
            library("fastutil", "it.unimi.dsi:fastutil:$fastutil")

            val jline: String by settings
            library("jline", "org.jline:jline:$jline")

            val brigadier: String by settings
            library("brigadier", "com.mojang:brigadier:$brigadier")

            //
            // Machine dependencies
            //

            val paklet: String by settings
            library("paklet-api", "org.machinemc:paklet-api:$paklet")
            library("paklet-core", "org.machinemc:paklet-core:$paklet")
            library("paklet-processor", "org.machinemc:paklet-processor:$paklet")

            val cogwheel: String by settings
            library("cogwheel-core", "org.machinemc:cogwheel-core:$cogwheel")
            library("cogwheel-json", "org.machinemc:cogwheel-json:$cogwheel")
            library("cogwheel-properties", "org.machinemc:cogwheel-properties:$cogwheel")
            library("cogwheel-yaml", "org.machinemc:cogwheel-yaml:$cogwheel")

            val landscape: String by settings
            library("landscape", "org.machinemc:landscape:$landscape")

            val nbt: String by settings
            library("nbt-core", "org.machinemc:nbt-core:$nbt")
            library("nbt-parser", "org.machinemc:nbt-parser:$nbt")

            val scriptive: String by settings
            library("scriptive-core", "org.machinemc:scriptive-core:$scriptive")
            library("scriptive-gson", "org.machinemc:scriptive-gson:$scriptive")
            library("scriptive-nbt", "org.machinemc:scriptive-nbt:$scriptive")

            val barebones: String by settings
            library("barebones-key", "org.machinemc:barebones-key:$barebones")
            library("barebones-profile", "org.machinemc:barebones-profile:$barebones")

            //
            // Plugins
            //

            val shadow: String by settings
            plugin("shadow", "io.github.goooler.shadow").version(shadow)

            val cadixdevLicenser: String by settings
            plugin("licenser", "org.cadixdev.licenser").version(cadixdevLicenser)

            val checkstyle: String by settings
            plugin("checkstyle", "checkstyle").version(checkstyle)

            plugin("paklet-plugin", "paklet-plugin").version(paklet)
        }

    }
}
