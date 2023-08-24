rootProject.name = "Machine"

pluginManagement {
    includeBuild("build-logic")
    includeBuild("code-generators")
}

include("api")
include("server")
include("application")
