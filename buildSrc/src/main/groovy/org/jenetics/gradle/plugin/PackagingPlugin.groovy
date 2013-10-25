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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar

import org.apache.tools.ant.filters.ReplaceTokens

import org.jenetics.gradle.Version

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__version__@
 * @version @__version__@ &mdash; <em>$Date: 2013-09-24 $</em>
 */
class PackagingPlugin implements Plugin<Project> {

	private static final def TEXT_FILE_PATTERN = [
		'**/*.java',
		'**/*.gradle',
		'**/*.cpp',
		'**/*.hpp',
		'**/*.c',
		'**/*.h',
		'**/*.bat',
		'**/*.sh',
		'**/*.txt',
		'**/*.properties',
		'**/*.md',
		'**/*.log'
	]

	private final Calendar now = Calendar.getInstance()
	private final int year = now.get(Calendar.YEAR)
	private final String copyrightYear = "2007-${year}"

	private Project project = null
	private String identifier = null
	private String version = null
	private File buildDir = null

	private def textContentReplacements = [:]

	@Override
	void apply(final Project project) {
		this.project = project

		version = project.rootProject.version
		buildDir = project.rootProject.buildDir
		identifier = "${project.rootProject.name}-${version}"
		textContentReplacements = [
			__identifier__: identifier,
			__year__: copyrightYear
		]

		project.extensions.create('packaging', PackagingPluginExtension)

		if (project.plugins.hasPlugin('java')) {
			jarjar()
		}
		packaging()
	}

	private boolean isRootProject() {
		return project == project.rootProject
	}

	private void jarjar() {
		project.task('jarjar', type: Jar, dependsOn: 'jar') {
			baseName = "${project.name}-all"

			from project.files(project.sourceSets.main.output.classesDir)
			from {
				project.configurations.compile.collect {
					it.isDirectory() ? it : project.zipTree(it)
				}
			}

			manifest {
				attributes(
					'Implementation-Title': "${project.name}-all",
					'Implementation-Versionv': project.version,
					'Implementation-URL': project.packaging.url,
					'Implementation-Vendor': project.packaging.author,
					'ProjectName': project.packaging.name,
					'Version': project.version,
					'Maintainer': project.packaging.author
				)
			}
		}
	}

	private void packaging() {
		project.task('packaging') {
			ext {
				identifier = this.identifier
				exportDir = new File("${buildDir}/package/${identifier}")
				exportProjectDir = new File("${exportDir}/project")
				exportProjectLibDir = new File("${exportProjectDir}/buildSrc/lib")
				exportLibDir = new File("${exportDir}/lib")
				exportJavadocDir = new File("${exportDir}/javadoc")
				exportReportDir = new File("${exportDir}/report")
				exportScriptDir = new File("${exportDir}/script")
			}

			doLast {
				if (isRootProject()) {
					copyRoot()
				} else {
					copy()
				}
			}
		}
	}

	private void copy() {
		println("Copying '${project}'")
	}

	private void copyRoot() {
		def task = project.tasks.getByName('packaging')

		// Copy the files in the root directory.
		project.copy {
			from('.') {
				include '*'
				excludes = [
					'org.*', '.gradle', 'gradle-app.setting', '.hgignore',
					'.hgtags', '*.iml', '*.ipr', '*.iws', '.project',
					'.classpath', '.settings', 'build', 'out'
				]
			}
			into task.exportProjectDir
			filter(ReplaceTokens, tokens: textContentReplacements)
		}

		copyDir('gradle', task.exportProjectDir)
	}

	private void copyDir(final String source, final File target) {
		// Copy the text files with text pattern replacement.
		project.copy {
			from(source) {
				includes = TEXT_FILE_PATTERN
				into source
			}
			into target
			filter(ReplaceTokens, tokens: textContentReplacements)
		}

		// Copy the rest, without replacement.
		project.copy {
			from(source) {
				excludes = TEXT_FILE_PATTERN
				into source
			}
			into target
		}
	}

}

class PackagingPluginExtension {
	String name = 'Jenetics'
	String author = 'Franz Wilhelmstötter'
	String url = 'http://jenetics.sourceforge.net'
}
































