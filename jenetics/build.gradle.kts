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

import org.apache.tools.ant.filters.ReplaceTokens

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.2
 * @version !__version__!
 */
plugins {
	`java-library`
	idea
	`maven-publish`
	signing
	id("me.champeau.gradle.jmh")
}

description = Jenetics.DESCRIPTION

extra["moduleName"] = "io.jenetics.base"

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

java {
	withJavadocJar()
	withSourcesJar()
}

tasks.named<Jar>("sourcesJar") {
	filter(
		ReplaceTokens::class, "tokens" to mapOf(
			"__identifier__" to "${Jenetics.NAME}-${Jenetics.VERSION}",
			"__year__" to Env.COPYRIGHT_YEAR
		)
	)
}

tasks.named<Jar>("javadocJar") {
	filter(
		ReplaceTokens::class, "tokens" to mapOf(
		"__identifier__" to "${Jenetics.NAME}-${Jenetics.VERSION}",
		"__year__" to Env.COPYRIGHT_YEAR
	)
	)
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = Jenetics.ID
			from(components["java"])
			versionMapping {
				usage("java-api") {
					fromResolutionOf("runtimeClasspath")
				}
				usage("java-runtime") {
					fromResolutionResult()
				}
			}
			pom {
				name.set(Jenetics.ID)
				description.set(Jenetics.DESCRIPTION)
				url.set(Jenetics.URL)
				inceptionYear.set("2007")

				properties.set(mapOf(
					"myProp" to "value",
					"prop.with.dots" to "anotherValue"
				))
				licenses {
					license {
						name.set("The Apache License, Version 2.0")
						url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
						distribution.set("repo")
					}
				}
				developers {
					developer {
						id.set(Jenetics.ID)
						name.set(Jenetics.AUTHOR)
						email.set(Jenetics.EMAIL)
					}
				}
				scm {
					connection.set(Jenetics.MavenScmConnection)
					developerConnection.set(Jenetics.MavenScmDeveloperConnection)
					url.set(Jenetics.MavenScmUrl)
				}
			}
		}
	}
	repositories {
		maven {
			url = if (version.toString().endsWith("SNAPSHOT")) {
				uri(Maven.SNAPSHOT_URL)
			} else {
				uri(Maven.RELEASE_URL)
			}

			credentials {
				username = if (extra.properties["nexus_username"] != null) {
					extra.properties["nexus_username"] as String
				} else {
					"nexus_username"
				}
				password = if (extra.properties["nexus_password"] != null) {
					extra.properties["nexus_password"] as String
				} else {
					"nexus_password"
				}
			}
		}
	}
}

signing {
	sign(publishing.publications["mavenJava"])
}
