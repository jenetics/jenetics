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
 * @version 1.5 &mdash; <em>$Date: 2014-10-03 $</em>
 */
class SetupPlugin extends JeneticsPlugin {

	private Calendar now = Calendar.getInstance()
	private int year = now.get(Calendar.YEAR)
	private String copyrightYear = "2007-${year}"
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm")

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
		plugins.apply(EclipsePlugin)
		plugins.apply(IdeaPlugin)

		clean.doLast {
			file("${project.projectDir}/test-output").deleteDir()
		}

		if (!isBuildSrc()) {
			configureOsgi()
			configureTestReporting()
			configureJavadoc()
		}
	}

	private void configureOsgi() {
		plugins.apply(OsgiPlugin)
		project.jar {
			manifest {
				version = version
				symbolicName = project.name
				name = project.name
				instruction 'Bundle-Vendor', jenetics.author
				instruction 'Bundle-Description', jenetics.description
				instruction 'Bundle-DocURL', jenetics.url

				attributes(
					'Implementation-Title': name,
					'Implementation-Version': version,
					'Implementation-URL': jenetics.url,
					'Implementation-Vendor': jenetics.name,
					'ProjectName': project.jenetics.name,
					'Version': version,
					'Maintainer': jenetics.author
				)
			}
		}
	}

	private void configureTestReporting() {
		plugins.apply(JacocoPlugin)
		project.test {
			useTestNG {
				//parallel = 'tests' // 'methods'
				//threadCount = Runtime.runtime.availableProcessors() + 1
				//include '**/*Test.class'
				suites project.file(
                        "${project.projectDir}/src/test/resources/testng.xml"
                    )
			}
		}
		project.jacocoTestReport {
			reports {
				xml.enabled true
				csv.enabled true
			}
		}
		task('testReport', dependsOn: 'test') << {
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
					'http://download.oracle.com/javase/7/docs/api/'
				]
				windowTitle = "Jenetics ${project.version}"
				docTitle = "<h1>Jenetics ${project.version}</h1>"
				bottom = "&copy; ${copyrightYear} Franz Wilhelmst&ouml;tter  &nbsp;<i>(${dateformat.format(now.time)})</i>"
				stylesheetFile = project.file("${rootDir}/buildSrc/resources/javadoc/stylesheet.css")

				exclude 'org/*/internal/**'

				//options.addStringOption('-subpackages', 'org.jenetics')
				//options.addStringOption('-exclude', 'org.jenetics.internal.util')

				//group('Core API', ['org.jenetics']).
				//group('Utilities', ['org.jenetics.util', 'org.jenetics.stat'])
			}

			// Copy the doc-files.
			doLast {
				copy {
					from('src/main/java') {
						include 'org/**/doc-files/*.*'
					}
					includeEmptyDirs = false
					into destinationDir.path
				}
			}
		}

		task('colorize', type: ColorizerTask) {
			directory = file(project.javadoc.destinationDir.path)
		}

		task('java2html') {
			ext {
				destination = project.javadoc.destinationDir.path
			}

			doLast {
				javaexec {
					main = 'de.java2html.Java2Html'
					args = [
						'-srcdir', 'src/main/java',
						'-targetdir', "${destination}/src-html"
					]
					classpath = files("${rootDir}/buildSrc/lib/java2html.jar")
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
		'serial',
		//'try',
		'unchecked'
	]

}
