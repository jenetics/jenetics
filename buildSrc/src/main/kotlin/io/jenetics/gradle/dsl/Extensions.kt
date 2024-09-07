package io.jenetics.gradle.dsl

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.extra
import java.io.File

val Project.sourceSets: SourceSetContainer get() =
	this.extensions.getByType(JavaPluginExtension::class.java).sourceSets

val SourceSetContainer.main: SourceSet get() =
	this.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

val Project.moduleName: String get() =
	this.extra.get("moduleName")?.toString() ?: this.name

val Project.isModule: Boolean get() = this.extra.has("moduleName")

val Project.allJava: FileTree get() = this.sourceSets.main.allJava.asFileTree

val List<Project>.allJava: FileTree get() =
	this.map { it.allJava }
		.reduce { a, b -> a.plus(b) }

val Project.compileClasspath: FileCollection get() =
	this.sourceSets.main.compileClasspath

val List<Project>.compileClasspath: FileCollection get() =
	this.map { it.compileClasspath }
		.reduce { a, b -> a.plus(b) }

val Project.snippetPaths: Set<String> get() =
	File("${project.projectDir}/src/main/java").walk()
		.filter { file -> file.isDirectory && file.endsWith("snippet-files") }
		.map { it.absolutePath }
		.toSet()

val Project.snippetPath: String? get() =
	this.snippetPaths
		.joinToString(separator = File.pathSeparator)
		.ifEmpty { null }

val List<Project>.snippetPath: String? get() =
	this.flatMap { it.snippetPaths }
		.joinToString(separator = File.pathSeparator)
		.ifEmpty { null }

