import org.machinemc.generators.LibraryGeneratorPlugin

plugins {
    `java-library`
}

apply<LibraryGeneratorPlugin>()

dependencies {

    sequenceOf(
            "machine-materials",
            "machine-blockdata",
    ).forEach {
        implementation(files("libs/$it.jar"))
    }

}