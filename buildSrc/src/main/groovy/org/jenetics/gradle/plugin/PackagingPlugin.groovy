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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__version__@
 * @version @__version__@ &mdash; <em>$Date: 2013-09-24 $</em>
 */
class PackagingPlugin implements Plugin<Project> {

	@Override
	void apply(final Project project) {
		project.extensions.create('packaging', PackagingPluginExtension)

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

}

class PackagingPluginExtension {
	String name = 'Jenetics'
	String author = 'Franz Wilhelmstötter'
	String url = 'http://jenetics.sourceforge.net'
}






