import java.io.FileInputStream
import java.util.Properties

dependencyResolutionManagement {

    versionCatalogs {
        val properties = Properties()
        properties.load(FileInputStream(File(rootDir, "../gradle.properties")))

        create("libs") {
            val cadixdevLicenser: String by properties
            library("licenser", "gradle.plugin.org.cadixdev.gradle:licenser:$cadixdevLicenser")
        }

    }

}