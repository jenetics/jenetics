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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.osgi.OsgiPlugin
import org.gradle.api.plugins.scala.ScalaPlugin
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.jenetics.gradle.task.ColorizerTask

import java.text.SimpleDateFormat

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version 1.5
 */
class SetupPlugin extends JeneticsPlugin {

	private Calendar now = Calendar.getInstance()
	private int year = now.get(Calendar.YEAR)
	private String copyrightYear = "2007-${year}"
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")

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
			configureOsgi()
			configureTestReporting()
			configureJavadoc()
		}
	}

	private void configureOsgi() {
		project.plugins.apply(OsgiPlugin)
		project.jar {
			manifest {
				version = version
				symbolicName = project.name
				name = project.name
				instruction 'Bundle-Vendor', project.jenetics.author
				instruction 'Bundle-Description', project.jenetics.description
				instruction 'Bundle-DocURL', project.jenetics.url

				attributes(
					'Implementation-Title': project.name,
					'Implementation-Version': project.version,
					'Implementation-URL': project.jenetics.url,
					'Implementation-Vendor': project.jenetics.name,
					'ProjectName': project.jenetics.name,
					'Version': project.version,
					'Maintainer': project.jenetics.author
				)
			}
		}
	}

	private void configureTestReporting() {
		project.plugins.apply(JacocoPlugin)
		project.test {
			outputs.upToDateWhen { false }
			useTestNG {
				parallel = 'tests' // 'methods'
				threadCount = Math.max(
					Runtime.runtime.availableProcessors() + 1,
					4
				)
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
		project.task('testReport', dependsOn: 'test') << {
			project.jacocoTestReport.execute()
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
				links = [
					'https://docs.oracle.com/javase/8/docs/api'
				]
				windowTitle = "Jenetics ${project.version}"
				docTitle = "<h1>Jenetics ${project.version}</h1>"
				bottom = "&copy; ${copyrightYear} Franz Wilhelmst&ouml;tter  &nbsp;<i>(${dateFormat.format(now.time)})</i>"
				stylesheetFile = project.file("${project.rootDir}/buildSrc/resources/javadoc/stylesheet.css")

				exclude '**/internal/**'

				//options.addStringOption('subpackages', 'org.jenetics')
                //options.addStringOption('excludedocfilessubdir', 'org/jenetics/internal')
				options.addStringOption('noqualifier', 'org.jenetics.internal.collection')

				group('Core API', ['org.jenetics', 'org.jenetics.engine'])
				group('Utilities', ['org.jenetics.util', 'org.jenetics.stat'])
			}

			// Copy the doc-files.
			doLast {
				project.copy {
					from('src/main/java') {
						include 'org/**/doc-files/*.*'
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
			project.colorize.execute()
			project.java2html.execute()
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
		//'serial',
		//'try',
		'unchecked'
	]

}
