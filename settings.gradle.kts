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
 * @version 6.3
 */
pluginManagement {
	repositories {
		mavenLocal()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	versionCatalogs {
		create("libs") {
			library("assertj", "org.assertj:assertj-core:3.24.2")
			library("commons-math", "org.apache.commons:commons-math3:3.6.1")
			library("equalsverifier", "nl.jqno.equalsverifier:equalsverifier:3.15.1")
			library("guava", "com.google.guava:guava:32.1.2-jre")
			library("jexl", "org.apache.commons:commons-jexl3:3.3")
			library("jpx", "io.jenetics:jpx:3.0.1")
			library("mvel", "org.mvel:mvel2:2.5.0.Final")
			library("nashorn", "org.openjdk.nashorn:nashorn-core:15.4")
			library("prngine", "io.jenetics:prngine:2.0.0")
			library("rxjava", "io.reactivex.rxjava2:rxjava:2.2.21")
			library("testng", "org.testng:testng:7.8.0")
		}
	}
}

rootProject.name = "jenetics"

// The Jenetics modules.
include("jenetics")
include("jenetics.ext")
include("jenetics.prog")
include("jenetics.xml")

// Non published modules.
include("jenetics.doc")
include("jenetics.example")
include("jenetics.tool")

// Incubation code
include("jenetics.incubator")
