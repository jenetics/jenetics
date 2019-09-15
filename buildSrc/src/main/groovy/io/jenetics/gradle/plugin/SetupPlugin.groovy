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
package io.jenetics.gradle.plugin

import io.jenetics.gradle.task.ColorizerTask
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.scala.ScalaPlugin
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.testing.jacoco.plugins.JacocoPlugin

import java.time.Year
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version 4.4
 */
class SetupPlugin extends JeneticsPlugin {

	private ZonedDateTime now = ZonedDateTime.now()
	private Year year = Year.now()
	private String copyrightYear = "2007-${year}"
	private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

	@Override
	void apply(final Project project) {
		super.apply(project)

		if (hasScalaSources()) {
			project.plugins.apply(ScalaPlugin)
			applyJava()
		} else if (hasJavaSources()) {
			project.plugins.apply(JavaPlugin)
			applyJava()
		}
		if (hasGroovySources()) {
			project.plugins.apply(GroovyPlugin)
		}
		if (hasLyxSources()) {
			project.plugins.apply(LyxPlugin)
		}

		project.tasks.withType(JavaCompile) { JavaCompile compile ->
			compile.options.encoding = 'UTF-8'
		}
		project.tasks.withType(JavaCompile) { JavaCompile compile ->
			compile.options.compilerArgs = ["-Xlint:${XLINT_OPTIONS.join(',')}"]
		}
	}

	private void applyJava() {
		project.plugins.apply(EclipsePlugin)
		project.plugins.apply(IdeaPlugin)

		project.clean.doLast {
			project.file("${project.projectDir}/test-output").deleteDir()
		}

		if (!isBuildSrc()) {
			configureTestReporting()
			configureJavadoc()
		}
	}

	private void configureTestReporting() {
		project.plugins.apply(JacocoPlugin)
		project.test {
			useTestNG {
				parallel = 'tests' // 'methods'
				threadCount = Runtime.runtime.availableProcessors() + 1
				if (project.hasProperty('excludeGroups')) {
					excludeGroups project.excludeGroups
				}
			}
		}

		project.jacocoTestReport {
			reports {
				xml.enabled true
				csv.enabled true
			}
		}

		project.task('testReport', dependsOn: 'test').doLast {
			if (project.file(project.jacoco.reportsDir).exists()) {
				project.jacocoTestReport.actions.each { Action action ->
					action.execute(project.jacocoTestReport)
				}
			}
		}
	}

	private void configureJavadoc() {
		project.javadoc {
			project.configure(options) {
				memberLevel = 'PROTECTED'
				version = true
				author = true
				docEncoding = 'UTF-8'
				charSet = 'UTF-8'
				linkSource = true
				linksOffline 'https://docs.oracle.com/javase/8/docs/api',
					"$project.rootDir/buildSrc/resources/javadoc"
				windowTitle = "Jenetics ${project.version}"
				docTitle = "<h1>Jenetics ${project.version}</h1>"
				bottom = "&copy; ${copyrightYear} Franz Wilhelmst&ouml;tter  &nbsp;<i>(${dateFormat.format(now)})</i>"
				stylesheetFile = project.file("${project.rootDir}/buildSrc/resources/javadoc/stylesheet.css")

				exclude '**/internal/**'

				//options.addStringOption('subpackages', 'io.jenetics')
                //options.addStringOption('excludedocfilessubdir', 'org/jenetics/internal')
				options.addStringOption('noqualifier', 'io.jenetics.internal.collection')
				options.tags = ["apiNote:a:API Note:",
								"implSpec:a:Implementation Requirements:",
								"implNote:a:Implementation Note:"]

				group('Core API', ['io.jenetics', 'io.jenetics.engine'])
				group('Utilities', ['io.jenetics.util', 'io.jenetics.stat'])
			}

			// Copy the doc-files.
			doLast {
				project.copy {
					from('src/main/java') {
						include 'io/**/doc-files/*.*'
					}
					includeEmptyDirs = false
					into destinationDir.path
				}
			}
		}

		project.task('colorize', type: ColorizerTask) {
			directory = project.file(project.javadoc.destinationDir.path)
		}

		project.task('java2html') {
			ext {
				destination = project.javadoc.destinationDir.path
			}

			doLast {
				project.javaexec {
					main = 'de.java2html.Java2Html'
					args = [
						'-srcdir', 'src/main/java',
						'-targetdir', "${destination}/src-html"
					]
					classpath = project.files("${project.rootDir}/buildSrc/lib/java2html.jar")
				}

			}
		}

		project.javadoc.doLast {
			project.colorize.actions.each { Action action ->
				action.execute(project.colorize)
			}
			project.java2html.actions.each { Action action ->
				action.execute(project.java2html)
			}
		}
	}

	private static final List<String> XLINT_OPTIONS = [
		'cast',
		'classfile',
		'deprecation',
		'dep-ann',
		'divzero',
		'finally',
		'overrides',
		'rawtypes',
		'serial',
		'try',
		'unchecked'
	]

}
