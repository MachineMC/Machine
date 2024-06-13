import org.machinemc.CheckStyleProvider
import org.machinemc.LicenseProvider

//
// Plugins
//

plugins {
    java
    `java-library`
    checkstyle
    id("org.cadixdev.licenser")
}

//
// Project info
//

val group: String by project
setGroup(group)

val version: String by project
setVersion(version)

//
// Repositories and Dependencies
//

val libs = project.rootProject
        .extensions
        .getByType(VersionCatalogsExtension::class)
        .named("libs")

repositories {
    mavenCentral()
    maven {
        name = "machinemcRepositoryReleases"
        url = uri("https://repository.machinemc.org/releases")
    }
}

dependencies {
    compileOnly(libs.findLibrary("jetbrains-annotations").get())

    compileOnly(libs.findLibrary("lombok").get())
    testCompileOnly(libs.findLibrary("lombok").get())
    testAnnotationProcessor(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())

    testImplementation(libs.findLibrary("junit-api").get())
    testRuntimeOnly(libs.findLibrary("junit-engine").get())
    testImplementation(libs.findLibrary("junit-params").get())
}

//
// Java configuration
//

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
}

//
// Checkstyle configuration
//

checkstyle {
    toolVersion = libs.findPlugin("checkstyle").get().get().version.toString()
    config = resources.text.fromUri(CheckStyleProvider.get())
}

dependencies {
    modules {
        // Replace old dependency `google-collections` with `guava`
        // This is required for checkstyle to work
        module("com.google.collections:google-collections") {
            replacedBy("com.google.guava:guava", "google-collections is part of guava")
        }
    }
}

//
// License configuration
//

license {
    header.set(project.resources.text.fromUri(LicenseProvider.get()))
    newLine(false)
    include("**/*.java")
}

//
// Task configurations
//

tasks {
    jar {
        dependsOn(updateLicenses)
    }
    withType<JavaCompile> {
        options.release.set(21)
        options.encoding = Charsets.UTF_8.name()
        // Can be used for debugging
        // options.compilerArgs.addAll(listOf("-Xlint:preview", "-Xlint:unchecked", "-Xlint:deprecation"))
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
    test {
        useJUnitPlatform()
    }
}
