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

import java.io.File

import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.Plugin
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
import org.jenetics.gradle.task.Lyx2PDFTask

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__version__@
 * @version @__version__@ &mdash; <em>$Date$</em>
 */
class SetupPlugin implements Plugin<Project> {

	private Project _project

	@Override
	void apply(final Project project) {
		_project = project

		if (hasScalaSources()) {
			_project.plugins.apply(ScalaPlugin)
			applyJava()
		}
		if (hasJavaSources() && !hasScalaSources()) {
			_project.plugins.apply(JavaPlugin)
			applyJava()
		}
		if (hasGroovySources()) {
			_project.plugins.apply(GroovyPlugin)
		}
		if (hasLyxSources()) {
			applyLyx()
		}

		_project.plugins.apply(PackagingPlugin)

		_project.tasks.withType(JavaCompile) { JavaCompile compile ->
			compile.options.encoding = 'UTF-8'
		}
		_project.tasks.withType(JavaCompile) { JavaCompile compile ->
			compile.options.compilerArgs = ["-Xlint:${XLINT_OPTIONS.join(',')}"]
		}
	}

	private void applyJava() {
		_project.plugins.apply(EclipsePlugin)
		_project.plugins.apply(IdeaPlugin)

		configureOsgi()
		configureTestReporting()
		configureJavadoc()
	}

	private void applyLyx() {
		_project.task('build') << {
			_project.copy {
				from("${_project.projectDir}/src/main") {
					include 'lyx/manual.lyx'
				}
				into temporaryDir
				filter(ReplaceTokens, tokens: [
					__identifier__: _project.identifier,
					__year__: _project.copyrightYear,
					__identifier__: _project.manualIdentifier
				])
			}
			_project.copy {
				from("${_project.projectDir}/src/main") {
					exclude 'lyx/manual.lyx'
				}
				into temporaryDir
			}
		}

		_project.task('lyx', type: Lyx2PDFTask) {
			document = _project.file("${_project.build.temporaryDir}/lyx/manual.lyx")
			doLast {
				_project.copy {
					from "${_project.build.temporaryDir}/lyx/manual.pdf"
					into "${_project.buildDir}/doc"
					rename { String fileName ->
						fileName.replace('manual.pdf', "manual-${_project.version}.pdf")
					}
				}
			}
		}
		_project.build.doLast {
			_project.lyx.execute()
		}
	}

	private void configureOsgi() {
		_project.plugins.apply(OsgiPlugin)
		_project.jar {
			manifest {
				version = version
				symbolicName = project.name
				name = project.name
				instruction 'Bundle-Vendor', _project.jenetics.author
				instruction 'Bundle-Description', _project.jenetics.description
				instruction 'Bundle-DocURL', _project.jenetics.url

				attributes(
					'Implementation-Title': _project.name,
					'Implementation-Version': version,
					'Implementation-URL': _project.jenetics.url,
					'Implementation-Vendor': _project.jenetics.name,
					'ProjectName': _project.jenetics.name,
					'Version': version,
					'Maintainer': _project.jenetics.author
				)
			}
		}
	}

	private void configureTestReporting() {
		_project.plugins.apply(JacocoPlugin)
		_project.test {
			useTestNG {
				//parallel = 'tests' // 'methods'
				//threadCount = Runtime.runtime.availableProcessors() + 1
				//include '**/*Test.class'
				suites 'src/test/resources/testng.xml'
			}
		}
		_project.jacocoTestReport {
			reports {
				xml.enabled true
				csv.enabled true
			}
		}
		_project.task('testReport', dependsOn: 'test') << {
			_project.jacocoTestReport.execute()
		}
	}

	private void configureJavadoc() {
		_project.javadoc {
			_project.configure(options) {
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
				bottom = "&copy; ${_project.copyrightYear} Franz Wilhelmst&ouml;tter  &nbsp;<i>(${_project.dateformat.format(_project.now.time)})</i>"
				stylesheetFile = _project.file("${_project.rootDir}/buildSrc/resources/javadoc/stylesheet.css")

				exclude 'org/*/internal/**'

				//options.addStringOption('-subpackages', 'org.jenetics')
				//options.addStringOption('-exclude', 'org.jenetics.internal.util')

				//group('Core API', ['org.jenetics']).
				//group('Utilities', ['org.jenetics.util', 'org.jenetics.stat'])
			}
		}

		_project.task('colorize', type: ColorizerTask) {
			directory = _project.file(_project.javadoc.destinationDir.path)
		}

		_project.task('java2html') {
			ext {
				destination = _project.javadoc.destinationDir.path
			}

			doLast {
				_project.javaexec {
					main = 'de.java2html.Java2Html'
					args = [
						'-srcdir', 'src/main/java',
						'-targetdir', "${destination}/src-html"
					]
					classpath = _project.files("${_project.rootDir}/buildSrc/lib/java2html.jar")
				}
				_project.copy {
					from 'src/main/java/org/*/doc-files'
					into "${destination}/org/*/doc-files"
				}
				_project.copy {
					from 'src/main/java/org/*/stat/doc-files'
					into "${destination}/org/*/stat/doc-files"
				}
				_project.copy {
					from 'src/main/java/org/*/util/doc-files'
					into "${destination}/org/*/util/doc-files"
				}
			}
		}

		_project.javadoc.doLast {
			_project.colorize.execute()
			_project.java2html.execute()
		}
	}

	private boolean hasJavaSources() {
		hasSources('java')
	}

	private boolean hasGroovySources() {
		hasSources('groovy')
	}

	private boolean hasScalaSources() {
		hasSources('scala')
	}

	private boolean hasLyxSources() {
		hasSources('lyx')
	}

	private boolean hasSources(final String source) {
		def srcDir = _project.file("${_project.projectDir}/src/main/${source}")
		def testDir = _project.file("${_project.projectDir}/src/test/${source}")

		srcDir.isDirectory() || testDir.isDirectory()
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


