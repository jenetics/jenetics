/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *   Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 *
 */

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.9
 * @version !__version__!
 */
plugins {
	`java-library`
	idea
	packaging
	id("me.champeau.gradle.jmh")
}

val moduleName = "io.jenetics.prog"

dependencies {
	api(project(":jenetics"))
	api(project(":jenetics.ext"))

	testImplementation(Libs.TestNG)
	testImplementation(Libs.EqualsVerifier)
}

tasks.test.get().dependsOn(tasks.compileJmhJava)

tasks.jar {
	manifest {
		attributes("Automatic-Module-Name" to moduleName)
	}
}

jmh {
	//jmhVersion = "1.20"
	//duplicateClassesStrategy = "include"
	warmupIterations = 2
	iterations = 4
	fork = 1
	include = listOf(".*MathExprPerf.*")
}

tasks.javadoc {
	val doclet = options as StandardJavadocDocletOptions
	doclet.linksOffline(
		"https://jenetics.io/javadoc/jenetics",
		"${project.rootDir}/buildSrc/resources/javadoc/jenetics.base"
	)
	doclet.linksOffline(
		"https://jenetics.io/javadoc/jenetics.ext",
		"${project.rootDir}/buildSrc/resources/javadoc/jenetics.ext"
	)
}

packaging {
	name = Jenetics.Ext.Name
	author = Jenetics.AUTHOR
	url = Jenetics.URL

	jarjar = false
	javadoc = true
}

//modifyPom {
//	project {
//		name "jentics.prog"
//		description "Jenetics Genetic Programming Module"
//		url project.property("jenetics.Url")
//		inceptionYear "2017"
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



