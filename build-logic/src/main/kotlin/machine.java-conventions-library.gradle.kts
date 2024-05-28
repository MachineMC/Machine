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
        url = uri("https://repo.machinemc.org/releases")
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
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
        options.compilerArgs = listOf("-Xlint:unchecked", "-Xlint:deprecation")
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
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    tasks.withType<Tar> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    tasks.withType<Zip> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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