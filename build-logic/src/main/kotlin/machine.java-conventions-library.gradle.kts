plugins {
    java
    `java-library`
    checkstyle
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

val checkstyleDir: File = File(mainDir, "checkstyle")

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
}

checkstyle {
    toolVersion = "10.3.1"
    configDirectory.set(checkstyleDir)
    configFile = File(checkstyleDir, "sun_checks.xml")
}

tasks.register<Jar>("buildAll") {
    group = "build"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.isFile }.map { zipTree(it) }
    })
}