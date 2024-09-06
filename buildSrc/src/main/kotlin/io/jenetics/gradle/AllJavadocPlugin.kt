package io.jenetics.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.JavadocMemberLevel
import org.gradle.external.javadoc.JavadocOfflineLink
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.named
import java.io.File
import java.time.LocalDate


interface AllJavadocExtension {

    /**
     * The names of the modules which are used for the Javadoc html page.
     */
    val modules: ListProperty<String>

    /**
     * The style sheet for the Javadoc html page.
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
    val javadocOfflineLinks: ListProperty<JavadocOfflineLink>
}

val Project.sourceSets: SourceSetContainer
    get() = this.extensions.getByType(JavaPluginExtension::class.java).sourceSets

/**
 * Gradle Plugin which defines and configure the native [Javadoc] task.
 */
class AllJavadocPlugin : Plugin<Project> {
    private lateinit var project: Project

    private val taskDependencies = listOf(
        "javadoc",
        "compileTestJava"
    )

    override fun apply(project: Project) {
        this.project = project

        project.extensions.create<AllJavadocExtension>("alljavadoc")

        project.tasks.register("alljavadoc", Javadoc::class.java) {
            description = "Aggregates Javadoc over all modules."
            group = JavaBasePlugin.DOCUMENTATION_GROUP
        }
    }

    /**
     * Function which initializes the [Javadoc] task.
     */
    fun init() {
        configure(
            project, project.extensions.getByType(AllJavadocExtension::class.java),
            project.tasks.named<Javadoc>("alljavadoc").get()
        )
    }

    /**
     * Function which configures the provided [Javadoc] task.
     *
     * @param project the project that should be configured
     * @param extension the extension containing the desired modules
     * @param task the task that should be configured
     */
    private fun configure(project: Project, extension: AllJavadocExtension, task: Javadoc) {
        val projects = project.subprojects
			.filterNotNull()
            .filter { extension.modules.get().contains(it.name) }
            .toSet()

        project.subprojects.forEach { subproject ->
            taskDependencies.forEach { taskName ->
                subproject.tasks.findByName(taskName)
					?.let {
						when (taskName) {
							"javadoc" -> task.dependsOn(it.taskDependencies)
							else -> task.dependsOn(it)
						}
					}
            }
        }

        if (projects.isNotEmpty()) {
            projects.forEach { proj ->
                proj.tasks.withType(Javadoc::class.java).forEach { javadoc ->
                    task.source += javadoc.source
                    task.classpath += javadoc.classpath
                    task.excludes += javadoc.excludes
                    task.includes += javadoc.includes
                }
            }
            task.excludes += extension.excludes.get()
            task.setDestinationDir(project.file(
				"${project.layout.buildDirectory.asFile.get()}/docs/alljavadoc"
			))
            task.title = "${project.name} documentation"
            task.options {
                this as StandardJavadocDocletOptions
                addStringOption("Xdoclint:none", "-quiet")
                addBooleanOption("Xdoclint:none", true)
                windowTitle = "${project.name.uppercase()} ${LocalDate.now()}"
                docTitle = "<h1>${project.name.uppercase()} ${LocalDate.now()}</h1>"
                memberLevel = JavadocMemberLevel.PROTECTED
                linkSource(false)
                docEncoding = "UTF-8"
                encoding = "UTF-8"
                charSet = "UTF-8"
                linksOffline = extension.javadocOfflineLinks.get()
                stylesheetFile = extension.styleSheet.orNull
                tags = listOf(
                    "apiNote:a:API Note:",
                    "implSpec:a:Implementation Requirements:",
                    "implNote:a:Implementation Note:"
                )
                projects.forEach {
                    group(it.name, getPackages(it).toList())
                }
            }
        }
    }

    /**
     * Function which parses all the fully-qualified names out of the given project.
     *
     * @param project the project that contains the javadoc candidates
     */
    private fun getPackages(project: Project): Set<String> =
        project.sourceSets.findByName("main")?.java
            ?.filter { it.path.endsWith(".java") }
            ?.map { it.toString()
                .substringAfter("src/main/java/")
                .substringBeforeLast(".java")
                .replace("/", ".")
                .substringBeforeLast(".")
            }?.toSet()
            ?: setOf()

}
