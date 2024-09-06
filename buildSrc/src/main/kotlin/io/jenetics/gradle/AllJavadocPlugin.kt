package io.jenetics.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.JavadocOfflineLink
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.extra
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path


interface AllJavadocExtension {

    /**
     * The names of the modules which are used for the Javadoc HTML page.
     */
    val modules: ListProperty<String>

    /**
     * The style sheet for the Javadoc HTML page.
     * Can be nullable
     */
    val styleSheet: Property<File?>

    /**
     * File patterns which are not included in the Javadoc page.
     */
    val excludes: ListProperty<String>

    /**
     * The offline links for the Javadoc page.
     */
    val linksOffline: ListProperty<JavadocOfflineLink>

	val options: Property<(StandardJavadocDocletOptions) -> Unit>

}

/**
 * Gradle Plugin which defines and configure the native [Javadoc] task.
 */
class AllJavadocPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extensions = project.extensions.create<AllJavadocExtension>("alljavadoc")

		project.gradle.projectsEvaluated {
			val modules: List<Project> = extensions.modules.get()
				.map { project.project(it) }

			project.tasks.register("alljavadoc", Javadoc::class.java) {
				modularity.inferModulePath.set(true)

				val opts = options as StandardJavadocDocletOptions

				// Setting sources and classpath.
				modules.forEach { module ->
					source += module.sourceSets.main.allJava
						.filter { !it.absolutePath.contains("internal") }
						.asFileTree
					classpath += module.sourceSets.main.compileClasspath
				}

				// Setting the Javadoc snippet paths.
				val snippetPaths: String? = modules
					.flatMap { it.snippetPaths }
					.joinToString(separator = File.pathSeparator)
					.ifEmpty { null }

				snippetPaths?.apply {
					opts.addStringOption("-snippet-path", this)
				}

				// Setting the module source path.
				val moduleSourcePaths = modules
					.map { "${it.moduleName}=${it.projectDir}/src/main/java" }

				opts.addMultilineStringsOption("-module-source-path")
					.value = moduleSourcePaths

				// Setting the user defined values.
				extensions.options.get()(opts)


				val excludedPackages: Set<String> = modules
					.flatMap { it.packages }
					.filter { matches(it, extensions.excludes.get()) }
					.toSet()

				if (excludedPackages.isNotEmpty()) {
					opts.addStringOption(
						"exclude",
						excludedPackages.joinToString(",")
					)
					opts.addStringOption(
						"noqualifier",
						excludedPackages.joinToString(",")
					)
				}


				setDestinationDir(project.file(
					"${project.layout.buildDirectory.asFile.get()}/docs/alljavadoc"
				))
			}
		}
    }

}

private val Project.sourceSets: SourceSetContainer get() =
	this.extensions.getByType(JavaPluginExtension::class.java).sourceSets

private val SourceSetContainer.main: SourceSet get() =
	this.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

private val Project.moduleName: String get() =
	this.extra.get("moduleName")?.toString() ?: this.name

private val SourceDirectorySet.dirs: String get() =
	this.toList()
		.filter { it.isDirectory }
		.distinct()
		.joinToString(separator = ":")

private val Project.snippetPaths: Set<String> get() =
	File("${project.projectDir}/src/main/java").walk()
		.filter { file -> file.isDirectory && file.endsWith("snippet-files") }
		.map { it.absolutePath }
		.toSet()

private val Project.packages: Set<String> get() =
	this.sourceSets.findByName("main")?.java
		?.filter { it.path.endsWith(".java") }
		?.filter { !it.path.endsWith("module-info.java") }
		?.map { it.toString()
			.substringAfter("src/main/java/")
			.substringBeforeLast(".java")
			.replace("/", ".")
			.substringBeforeLast(".") }
		?.toSet()
		?: setOf()

private fun matches(value: String, patterns: List<String>): Boolean {
	val fs = FileSystems.getDefault()
	val v = Path.of(value);

	return patterns.any { fs.getPathMatcher("glob:$it").matches(v) }
}
