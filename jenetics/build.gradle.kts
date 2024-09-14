import io.jenetics.gradle.dsl.moduleName

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
 * @version 6.1
 */
plugins {
	`java-library`
	`java-test-fixtures`
	`maven-publish`
	alias(libs.plugins.jmh)
}

moduleName = "io.jenetics.base"
description = "Jenetics - Java Genetic Algorithm Library"

dependencies {
	testImplementation(libs.assertj)
	testImplementation(libs.commons.math)
	testImplementation(libs.commons.rng.sampling)
	testImplementation(libs.commons.rng.simple)
	testImplementation(libs.equalsverifier)
	testImplementation(libs.jpx)
	testImplementation(libs.prngine)
	testImplementation(libs.testng)
	testImplementation(testFixtures(project(":jenetics")))

	testFixturesApi(libs.assertj)
	testFixturesApi(libs.commons.math)
	testFixturesApi(libs.testng)

	jmh(libs.prngine)
}

tasks.test { dependsOn(tasks.compileJmhJava) }

jmh {
	includes.add(".*ProxySorterPerf.*")
}
