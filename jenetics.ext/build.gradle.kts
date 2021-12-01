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
 * @since 3.5
 * @version 6.1
 */
plugins {
	`java-library`
	idea
	`maven-publish`
	id("me.champeau.jmh")
}

description = "Jenetics Extension"

extra["moduleName"] = "io.jenetics.ext"

dependencies {
	api(project(":jenetics"))

	testImplementation(libs.commons.math)
	testImplementation(libs.testng)
	testImplementation(libs.assertj)
	testImplementation(libs.equalsverifier)
	testImplementation(project(":jenetics").dependencyProject.sourceSets["test"].output)
}

tasks.compileTestJava { dependsOn(":jenetics:compileTestJava") }
tasks.test { dependsOn(tasks.compileJmhJava) }

jmh {
	includes.add(".*TreePerf.*")
}

tasks.javadoc {
	val doclet = options as StandardJavadocDocletOptions
	doclet.linksOffline(
		"https://jenetics.io/javadoc/jenetics",
		"${project.rootDir}/buildSrc/resources/javadoc/jenetics.base"
	)
}
