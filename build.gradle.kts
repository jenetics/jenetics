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
	signing
	packaging
}

rootProject.version = Jenetics.Version

/*
 * Project configuration *before* the projects has been evaluated.
 */
allprojects {
	group =  Jenetics.Group
	version = Jenetics.Version

	repositories {
		mavenLocal()
		mavenCentral()
		jcenter()
		flatDir {
			dirs("${rootDir}/buildSrc/lib")
		}
	}

	configurations.all {
		resolutionStrategy.preferProjectModules()
	}
}

/*
 * Project configuration *after* the projects has been evaluated.
 */
gradle.projectsEvaluated {
	subprojects {
		val project = this

		tasks.withType<JavaCompile> {
			options.compilerArgs.add("-Xlint:" + xlint())
		}

		plugins.withType<JavaPlugin> {
			configure<JavaPluginConvention> {
				sourceCompatibility = JavaVersion.VERSION_11
				targetCompatibility = JavaVersion.VERSION_11
			}

			setupTestReporting(project)
		}
	}

	tasks.withType<Jar> {
		manifest {
			from(manifest(project))
		}
	}
}

fun manifest(project: Project): Manifest {
	return the<JavaPluginConvention>().manifest {
		attributes (
			"Implementation-Title" to project.name,
			"Implementation-Version" to project.version.toString(),
			"Implementation-URL" to Jenetics.Url,
			"Implementation-Vendor" to Jenetics.Name,
			"ProjectName" to Jenetics.Name,
			"Version" to project.version.toString(),
			"Maintainer" to Jenetics.Author,
			"Project" to project.name,
			"Project-Version" to project.version,
			"Built-By" to System.getProperty("user.name"),
			"Build-Timestamp" to Env.dateformat.format(Env.now),
			"Created-By" to "Gradle ${gradle.gradleVersion}",
			"Build-Jdk" to "${System.getProperty("java.vm.name")} " +
			"(${System.getProperty("java.vm.vendor")} " +
				"${System.getProperty("java.vm.version")})",
			"Build-OS" to "${System.getProperty("os.name")} " +
			"${System.getProperty("os.arch")} " +
				System.getProperty("os.version")
		)
	}
}

fun setupTestReporting(project: Project) {
	project.apply(plugin = "jacoco")

	project.configure<JacocoPluginExtension> {
		toolVersion = "0.8.5"
		reportsDir = file("${buildDir}/jacocoReportDir")
	}

	project.tasks {
		named<JacocoReport>("jacocoTestReport") {
			dependsOn("test")

			reports {
				xml.isEnabled = true
				csv.isEnabled = false
			}
		}

		named<Test>("test") {
			useTestNG()
			finalizedBy("jacocoTestReport")
		}
	}
}

fun xlint(): String {
	// See https://docs.oracle.com/javase/9/tools/javac.htm#JSWOR627
	return listOf(
		"cast",
		"classfile",
		"deprecation",
		"dep-ann",
		"divzero",
		"empty",
		"finally",
		"overrides",
		"rawtypes",
		"serial",
		"static",
		"try",
		"unchecked"
	).joinToString(separator = ",")
}

tasks.register<Zip>("zip") {
	val identifier = "${Jenetics.Name}-${Jenetics.Version}"

	from("build/package/${identifier}") {
		into(identifier)
	}

	archiveBaseName.set(identifier)
	archiveVersion.set(Jenetics.Version)

	doLast {
		val zip = file("${identifier}.zip")
		zip.renameTo(file("build/package${zip.name}"))
	}
}
