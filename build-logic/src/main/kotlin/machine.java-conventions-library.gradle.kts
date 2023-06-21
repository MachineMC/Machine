plugins {
    java
    `java-library`
    checkstyle
    id("org.cadixdev.licenser")
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

lateinit var mainDir: File // Root directory of the Machine project
run {
    var current: File = projectDir
    while(!File(current, "gradlew").exists()) {
        current = current.parentFile
    }
    mainDir = current
}

repositories {
    mavenCentral()
    maven {
        url = uri("http://www.machinemc.org/releases")
        isAllowInsecureProtocol = true
    }
}

dependencies {
    compileOnly(libs.jetbrains.annotations)

    compileOnly(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.junit.params)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
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
    jar {
        dependsOn(updateLicenses)
    }
}

license {
    header(File(mainDir, "HEADER.txt"))
    newLine(false)
    include("**/*.java")
}

checkstyle {
    toolVersion = libs.versions.checkstyle.get()
    val checkstyleDir = File(mainDir, "checkstyle")
    configDirectory.set(checkstyleDir)
    configFile = File(checkstyleDir, "sun_checks.xml")
}

tasks.register<Jar>("buildAll") {
    group = "build"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    dependsOn(tasks.getByName("updateLicenses"))
    from({
        configurations.runtimeClasspath.get().filter { it.isFile }.map { zipTree(it) }
    })
}