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

import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version 3.4
 */
class PackagingPlugin implements Plugin<Project> {

	private static final String JARJAR = 'jarjar'
	private static final String PACKAGING = 'packaging'

	private final Calendar _now = Calendar.getInstance()
	private final int _year = _now.get(Calendar.YEAR)
	private final String _copyrightYear = "2007-${_year}"

	private Project _project = null
	private String _identifier = null
	private String _version = null

	private File _buildDir = null
	private File _exportDir = null
	private File _exportProjectDir = null
	private File _exportProjectLibDir = null
	private File _exportLibDir = null
	private File _exportJavadocDir = null
	private File _exportReportDir = null
	private File _exportScriptDir = null

	private def _textContentReplacements = [:]

	@Override
	void apply(final Project project) {
		_project = project
		_version = project.rootProject.version
		_identifier = "${project.rootProject.name}-${_version}"

		_buildDir = project.rootProject.buildDir
		_exportDir = new File("${_buildDir}/package/${_identifier}")
		_exportProjectDir = new File("${_exportDir}/project")
		_exportProjectLibDir = new File("${_exportProjectDir}/buildSrc/lib")
		_exportLibDir = new File("${_exportDir}/lib")
		_exportJavadocDir = new File("${_exportDir}/javadoc")
		_exportReportDir = new File("${_exportDir}/report")
		_exportScriptDir = new File("${_exportDir}/script")

		_textContentReplacements = [
			__identifier__: _identifier,
			__year__: _copyrightYear
		]

		// Packaging extension class.
		project.extensions.create(
			'packaging',
			PackagingPluginExtension,
			this
		)

		if (project.plugins.hasPlugin('java') &&
			_project.packaging.jarjar &&
			_project.name != 'buildSrc')
		{
			jarjar()
		}
		packaging()
	}

	private boolean isRootProject() {
		return _project == _project.rootProject
	}

	private void jarjar() {
		_project.task(JARJAR, type: Jar, dependsOn: 'jar') {
			appendix = 'jarjar'

			from _project.files(_project.sourceSets.main.output.classesDir)
			from {
				_project.configurations.compile.collect {
					it.isDirectory() ? it : _project.zipTree(it)
				}
			}

			doFirst {
				manifest {
					attributes(
						'Implementation-Title': "${_project.name}-${appendix}",
						'Implementation-Version': _project.version,
						'Implementation-URL': _project.packaging.url,
						'Implementation-Vendor': _project.packaging.author,
						'ProjectName': _project.packaging.name,
						'Version': _project.version,
						'Maintainer': _project.packaging.author
					)
				}
			}
		}
	}

	private void packaging() {
		def dependencies = []
		if (_project.tasks.findByPath(JARJAR) != null) {
			dependencies += JARJAR
		}
		if (_project.tasks.findByPath('build') != null) {
			dependencies += 'build'
		}
		if (_project.tasks.findByPath('testReport') != null) {
			dependencies += 'testReport'
		}
		if (_project.tasks.findByPath('javadoc') != null) {
			dependencies += 'javadoc'
		}

		_project.task(PACKAGING, dependsOn: dependencies) {
			ext {
				identifier = _identifier
				exportDir = _exportDir
				exportProjectDir = _exportProjectDir
				exportProjectLibDir = _exportProjectLibDir
				exportLibDir = _exportLibDir
				exportJavadocDir = _exportJavadocDir
				exportReportDir = _exportReportDir
				exportScriptDir = _exportScriptDir
			}

			doLast {
				if (isRootProject()) {
					copyRoot()
				} else {
					copy()
				}
			}
		}

		// Copy the test-reports
		if (_project.tasks.findByPath('testReport') != null) {
			_project.tasks.findByPath('testReport').doLast {
				copyDir(
					new File(_project.buildDir, 'reports'),
					_project.name,
					_exportReportDir
				)
			}
		}

		// Copy the javadoc.
		if (_project.tasks.findByPath('javadoc') != null) {
			_project.tasks.findByPath('javadoc').doLast {
				if (_project.packaging.javadoc) {
					copyDir(
						new File(_project.buildDir, 'docs/javadoc'),
						_project.name,
						_exportJavadocDir
					)
				}
			}
		}

		// Copy the pdf manual.
		if (_project.tasks.findByPath('lyx') != null) {
			_project.tasks.findByPath('build').doLast {
				_project.copy {
					from("${_project.buildDir}/doc") {
						include '*.pdf'
					}
					into _exportDir
				}
			}
		}

		// Copy jar dependencies for java builds.
		if (_project.plugins.hasPlugin('java') && _project.name != 'buildSrc') {
			_project.tasks.findByPath('build').doLast {
				// Copy the external jar dependencies.
				_project.configurations.testRuntime.each { jar ->
					if (jar.name.endsWith('.jar') &&
						!jar.name.startsWith('org.jeneti'))
					{
						_project.copy {
							from jar
							into _exportProjectLibDir
						}
					}
				}
			}

			if (_project.tasks.findByPath('jarjar') != null) {
				_project.tasks.findByPath('jarjar').doLast {
					// Copy the build library
					_project.copy {
						from("${_project.buildDir}/libs")
						if (!_project.packaging.jarjar) {
							exclude '*-jarjar*.jar'
						}
						into _exportLibDir
					}
				}
			}
		}

	}

	private void copy() {
		copyDir(_project.projectDir, _exportProjectDir)
	}

	private void copyRoot() {
		// Copy the files in the root directory.
		_project.copy {
			from('.') {
				include '*'
				excludes = IGNORED_FILES
			}
			includeEmptyDirs = false
			into _exportProjectDir
			filter(ReplaceTokens, tokens: _textContentReplacements)
		}

		copyDir(new File('gradle'), _exportProjectDir)
		copyDir(new File('buildSrc'), _exportProjectDir)
	}

	private void copyDir(final File source, final File target) {
		copyDir(source, source.name, target)
	}

	private void copyDir(
		final File source,
		final String sinto,
		final File target
	) {
		// Copy the text files with text pattern replacement.
		_project.copy {
			from(source.absoluteFile) {
				includes = TEXT_FILE_PATTERN
				excludes = IGNORED_FILES
				exclude { details ->
					details.file.absolutePath.contains('src/main/results') ||
					details.file.absolutePath.contains('src/test/results')
				}
				into sinto
			}
			includeEmptyDirs = false
			into target
			filter(ReplaceTokens, tokens: _textContentReplacements)
		}

		// Copy the rest, without replacement.
		_project.copy {
			from(source.absoluteFile) {
				excludes = TEXT_FILE_PATTERN + IGNORED_FILES
				exclude { details ->
					details.file.absolutePath.contains('src/main/results') ||
					details.file.absolutePath.contains('src/test/results')
				}
				into sinto
			}
			includeEmptyDirs = false
			into target
		}
	}

	void doFirstPackaging(final Closure closure) {
		_project.tasks.findByPath(PACKAGING).doFirst(closure)
	}

	void doLastPackaging(final Closure closure) {
		_project.tasks.findByPath(PACKAGING).doLast(closure)
	}

	private static final def IGNORED_FILES = [
		'bin/**',
		'build/**',
		'build.xml',
		'.classpath',
		'*.dblite',
		'.gradle/**',
		'gradle-app.setting',
		'.groovy/*',
		'.hgrc',
		'*.iml',
		'*.ipr',
		'*.iws',
		'manifest.mf',
		'nbbuild/**',
		'.nb-gradle/**',
		'nbproject/**',
		'out/**',
		'.project',
		'random-x86_64',
		'.settings/**',
		'*.so',
		'test-output/**',
		'wiki/**'
	]

	private static final def TEXT_FILE_PATTERN = [
		'**/*.bat',
		'**/*.c',
		'**/*.cpp',
		'**/*.gradle',
		'**/*.groovy',
		'**/*.h',
		'**/*.hpp',
		'**/*.html',
		'**/*.java',
		'**/*.log',
		'**/*.lyx',
		'**/*.md',
		'**/*.properties',
		'**/*.sh',
		'**/*.txt',
		'**/*.xml'
	]

}

class PackagingPluginExtension {

	private final PackagingPlugin _plugin

	PackagingPluginExtension(final PackagingPlugin plugin) {
		_plugin = plugin
	}

	String name = 'Jenetics'
	String author = 'Franz Wilhelmstötter'
	String url = 'http://jenetics.sourceforge.net'
	Boolean jar = true
	Boolean jarjar = true
	Boolean javadoc = true

	void doFirst(final Closure closure) {
		_plugin.doFirstPackaging(closure)
	}

	void doLast(final Closure closure) {
		_plugin.doLastPackaging(closure)
	}
}
