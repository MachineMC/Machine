rootProject.name = "Machine"

enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    includeBuild("build-logic")
    includeBuild("code-generators")
}
