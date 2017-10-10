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

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version 3.8
 */
class JeneticsPlugin implements Plugin<Project> {

	Project project

	@Override
	public void apply(final Project project) {
		this.project = project
	}

	protected boolean hasJavaSources() {
		hasSources('java')
	}

	protected boolean hasGroovySources() {
		hasSources('groovy')
	}

	protected boolean hasScalaSources() {
		hasSources('scala')
	}

	protected boolean hasLyxSources() {
		hasSources('lyx')
	}

	protected boolean isBuildSrc() {
		project.name == 'buildSrc'
	}

	protected boolean hasSources(final String source) {
		def srcDir = project.file("${project.projectDir}/src/main/${source}")
		def testDir = project.file("${project.projectDir}/src/test/${source}")

		srcDir.isDirectory() || testDir.isDirectory()
	}

}
