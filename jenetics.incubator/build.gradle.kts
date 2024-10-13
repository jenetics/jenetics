import io.jenetics.gradle.dsl.moduleName

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
 * @since 6.1
 * @version 6.1
 */
plugins {
	`java-library`
	idea
	//antlr
	alias(libs.plugins.jmh)
}

moduleName = "io.jenetics.incubator"
description = "Jenetics Genetic Incubator"

dependencies {
	api(project(":jenetics"))
	api(project(":jenetics.ext"))
	api(project(":jenetics.prog"))
	implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0")
	implementation(libs.commons.math)

	testImplementation(libs.assertj)
	testImplementation(libs.equalsverifier)
	testImplementation(libs.guava)
	testImplementation(libs.testng)
	testImplementation(libs.jpx)

	jmh(libs.commons.csv)
	jmh(libs.javacsv)
	jmh(libs.opencsv)
	jmh(libs.supercsv)
}

tasks.test { dependsOn(tasks.compileJmhJava) }

jmh {
	includes.add(".*ErfcPerf.*")
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
	doclet.linksOffline(
		"https://jenetics.io/javadoc/jenetics.prog",
		"${project.rootDir}/buildSrc/resources/javadoc/jenetics.prog"
	)
}
