pluginManagement {
    includeBuild("../build-logic")
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

include(":api")
project(":api").projectDir = file("../api")
include(":server")
project(":server").projectDir = file("../server")