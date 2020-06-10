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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.9
 * @version 4.4
 */

plugins {
	id("idea")
	id 'me.champeau.gradle.jmh'
}

apply plugin: 'java-library'
apply plugin: 'packaging'
apply plugin: 'nexus'

ext.moduleName = 'io.jenetics.xml'

dependencies {
	api project(':jenetics')

	testImplementation property('include.TestNG')
	testImplementation property('include.PRNGine')

	jmh project(':jenetics')
}

jmh {
	duplicateClassesStrategy = 'warn'
}

idea {
	module{
		scopes.COMPILE.plus += [configurations.jmh]
	}
}

jar.manifest.attributes('Automatic-Module-Name': 'io.jenetics.xml')

test.dependsOn(compileJmhJava)

javadoc {
	options {
		linksOffline(
			'https://jenetics.io/javadoc/jenetics',
			"${project.rootDir}/buildSrc/resources/javadoc/jenetics.base"
		)
	}
}

packaging {
	name = property('jenetics.xml.Name')
	author = property('jenetics.Author')
	url = property('jenetics.Url')

	jarjar = false
	javadoc = true
}

modifyPom {
	project {
		name 'jenetics.xml'
		description 'Jenetics XML marshalling module'
		url project.property('jenetics.Url')
		inceptionYear '2017'

		scm {
			url project.property('jenetics.MavenScmUrl')
			connection project.property('jenetics.MavenScmConnection')
			developerConnection project.property('jenetics.MavenScmDeveloperConnection')
		}

		licenses {
			license {
				name 'The Apache Software License, Version 2.0'
				url 'http://www.apache.org/licenses/LICENSE-2.0.txt'
				distribution 'repo'
			}
		}

		developers {
			developer {
				id project.property('jenetics.Id')
				name project.property('jenetics.Author')
				email project.property('jenetics.Email')
			}
		}
	}
}

//nexus {
//	identifier = project.identifier
//	copyrightYear = project.copyrightYear
//	attachSources = true
//	attachTests = false
//	attachJavadoc = true
//	sign = true
//	repository = project.property('build.MavenRepository')
//	snapshotRepository = project.property('build.MavenSnapshotRepository')
//}
