package me.pesekjak.machine.gradle

import me.pesekjak.machine.codegen.Generators
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class LibraryGeneratorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        Generators.run(project.projectDir)
    }

}