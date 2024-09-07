/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.gradle

import io.jenetics.gradle.dsl.allJava
import io.jenetics.gradle.dsl.compileClasspath
import io.jenetics.gradle.dsl.isModule
import io.jenetics.gradle.dsl.moduleName
import io.jenetics.gradle.dsl.snippetPathString
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.create

/**
 * Plugin which configures the {@code javaodc} task and adds an {@code alljavadoc}
 * tasks, which creates a combined Javadoc for all modules.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
interface AllJavadocExtension {

    /**
     * The names of the modules which are used for the Javadoc HTML page.
     */
    val modules: ListProperty<String>

	/**
	 * File filter for the Java files, which will be part of the generated
	 * Javadoc.
	 */
	val files: Property<(PatternFilterable) -> Unit>

	/**
	 * Configuration function for the standard Javadoc doclet.
	 */
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

				source += modules.allJava.matching {
					extensions.files.orNull?.invoke(this)
				}
				classpath += modules.compileClasspath

				val opts = options as StandardJavadocDocletOptions

				modules.snippetPathString?.apply {
					opts.addStringOption("-snippet-path", this)
				}

				opts.addMultilineStringsOption("-module-source-path")
					.value = modules.map { "${it.moduleName}=${it.projectDir}/src/main/java" }

				// Setting the user defined values.
				extensions.options.orNull?.invoke(opts)

				setDestinationDir(project.file(
					"${project.layout.buildDirectory.asFile.get()}/docs/alljavadoc"
				))
			}

			project.subprojects {
				val prj = this

				prj.tasks.named("javadoc", Javadoc::class.java) {
					source = prj.allJava.matching {
						extensions.files.orNull?.invoke(this)
					}
					classpath = prj.compileClasspath

					modularity.inferModulePath.set(true)

					val opts = options as StandardJavadocDocletOptions
					extensions.options.orNull?.invoke(opts)

					prj.snippetPathString?.apply {
						opts.addStringOption("-snippet-path", this)
					}

					if (prj.isModule) {
						opts.addStringsOption("-module-source-path")
							.value = listOf("${prj.moduleName}=${prj.projectDir}/src/main/java")
					}

					doLast {
						val dir = if (prj.isModule) { prj.moduleName } else { "" }

						prj.copy {
							from("src/main/java") {
								include("**/doc-files/*.*")
							}
							includeEmptyDirs = false
							into(destinationDir!!.resolve(dir))
						}
					}
				}
			}
		}

    }
}
