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
 * @since 1.2
 * @version !__version__!
 */
plugins {
	`java-library`
	idea
	packaging
	id("me.champeau.gradle.jmh")
}

description = Jenetics.DESCRIPTION

val moduleName = "io.jenetics.base"

dependencies {
	testImplementation(Libs.ApacheCommonsMath)
	testImplementation(Libs.TestNG)
	testImplementation(Libs.EqualsVerifier)
	testImplementation(Libs.PRNGine)

	jmh(Libs.PRNGine)
}

tasks.test.get().dependsOn(tasks.compileJmhJava)

jmh {
	include = listOf(".*IntegerChromosomePerf.*")
}

tasks.jar {
	manifest {
		attributes("Automatic-Module-Name" to moduleName)
	}
}

packaging {
	name = Jenetics.NAME
	author = Jenetics.AUTHOR
	url = Jenetics.URL

	jarjar = false
	javadoc = true
}

//modifyPom {
//	project {
//		name "jenetics"
//		description "Jenetics - Java Genetic Algorithm Library"
//		url project.property("jenetics.Url")
//		inceptionYear "2007"
//
//		scm {
//			url project.property("jenetics.MavenScmUrl")
//			connection project.property("jenetics.MavenScmConnection")
//			developerConnection project.property("jenetics.MavenScmDeveloperConnection")
//		}
//
//		licenses {
//			license {
//				name "The Apache Software License, Version 2.0"
//				url "http://www.apache.org/licenses/LICENSE-2.0.txt"
//				distribution "repo"
//			}
//		}
//
//		developers {
//			developer {
//				id project.property("jenetics.Id")
//				name project.property("jenetics.Author")
//				email project.property("jenetics.Email")
//			}
//		}
//	}
//}

//nexus {
//	identifier = project.identifier
//	copyrightYear = project.copyrightYear
//	attachSources = true
//	attachTests = false
//	attachJavadoc = true
//	sign = true
//	repository = project.property("build.MavenRepository")
//	snapshotRepository = project.property("build.MavenSnapshotRepository")
//}
