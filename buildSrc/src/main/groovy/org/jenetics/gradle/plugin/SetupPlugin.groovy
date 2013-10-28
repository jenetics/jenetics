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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__version__@
 * @version @__version__@ &mdash; <em>$Date: 2013-10-28 $</em>
 */
class SetupPlugin extends JeneticsPlugin {


	@Override
	void apply(final Project project) {
		super.apply(project)

		if (hasScalaSources()) {
			project.plugins.apply(ScalaPlugin)
			applyJava()
		}
		if (hasJavaSources() && !hasScalaSources()) {
			project.plugins.apply(JavaPlugin)
			applyJava()
		}
		if (hasGroovySources()) {
			project.plugins.apply(GroovyPlugin)
		}
		if (hasLyxSources()) {
			project.plugins.apply(LyxPlugin)
		}

		project.plugins.apply(PackagingPlugin)

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

		configureOsgi()
		configureTestReporting()
		configureJavadoc()
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
					'Implementation-Version': version,
					'Implementation-URL': project.jenetics.url,
					'Implementation-Vendor': project.jenetics.name,
					'ProjectName': project.jenetics.name,
					'Version': version,
					'Maintainer': project.jenetics.author
				)
			}
		}
	}

	private void configureTestReporting() {
		project.plugins.apply(JacocoPlugin)
		project.test {
			useTestNG {
				//parallel = 'tests' // 'methods'
				//threadCount = Runtime.runtime.availableProcessors() + 1
				//include '**/*Test.class'
				suites 'src/test/resources/testng.xml'
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
					'http://download.oracle.com/javase/7/docs/api/',
					'http://jscience.org/api/',
					'http://javolution.org/target/site/apidocs/'
				]
				windowTitle = "Jenetics ${project.version}"
				docTitle = "<h1>Jenetics ${project.version}</h1>"
				bottom = "&copy; ${project.copyrightYear} Franz Wilhelmst&ouml;tter  &nbsp;<i>(${project.dateformat.format(project.now.time)})</i>"
				stylesheetFile = project.file("${project.rootDir}/buildSrc/resources/javadoc/stylesheet.css")

				exclude 'org/*/internal/**'

				//options.addStringOption('-subpackages', 'org.jenetics')
				//options.addStringOption('-exclude', 'org.jenetics.internal.util')

				//group('Core API', ['org.jenetics']).
				//group('Utilities', ['org.jenetics.util', 'org.jenetics.stat'])
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
				project.copy {
					from 'src/main/java/org/*/doc-files'
					into "${destination}/org/*/doc-files"
				}
				project.copy {
					from 'src/main/java/org/*/stat/doc-files'
					into "${destination}/org/*/stat/doc-files"
				}
				project.copy {
					from 'src/main/java/org/*/util/doc-files'
					into "${destination}/org/*/util/doc-files"
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
		'try',
		'unchecked'
	]

}


