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

import io.jenetics.gradle.task.ColorizerTask

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.2
 * @version !__version__!
 */
plugins {
	signing
	packaging
}

rootProject.version = Jenetics.VERSION

/*
 * Project configuration *before* the projects has been evaluated.
 */
allprojects {
	group =  Jenetics.GROUP
	version = Jenetics.VERSION

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
			setupJavadoc(project)
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
			"Implementation-Version" to Jenetics.VERSION,
			"Implementation-URL" to Jenetics.URL,
			"Implementation-Vendor" to Jenetics.NAME,
			"ProjectName" to Jenetics.NAME,
			"Version" to Jenetics.VERSION,
			"Maintainer" to Jenetics.AUTHOR,
			"Project" to project.name,
			"Project-Version" to Jenetics.VERSION,
			"Built-By" to Env.USER_NAME,
			"Build-Timestamp" to Env.BUILD_TIME,
			"Created-By" to "Gradle ${gradle.gradleVersion}",
			"Build-Jdk" to Env.BUILD_JDK,
			"Build-OS" to Env.BUILD_OS
		)
	}
}

fun setupTestReporting(project: Project) {
	project.apply(plugin = "jacoco")

	project.configure<JacocoPluginExtension> {
		toolVersion = "0.8.5"
	}

	project.tasks {
		named<JacocoReport>("jacocoTestReport") {
			dependsOn("test")

			reports {
				xml.isEnabled = true
				csv.isEnabled = true
			}
		}

		named<Test>("test") {
			useTestNG()
			finalizedBy("jacocoTestReport")
		}
	}
}

fun setupJavadoc(project: Project) {
	project.tasks.withType<Javadoc> {
		val doclet = options as StandardJavadocDocletOptions

		exclude("**/internal/**")

		doclet.memberLevel = JavadocMemberLevel.PROTECTED
		doclet.version(true)
		doclet.docEncoding = "UTF-8"
		doclet.charSet = "UTF-8"
		doclet.linkSource(true)
		doclet.linksOffline(
				"https://docs.oracle.com/en/java/javase/11/docs/api",
				"${project.rootDir}/buildSrc/resources/javadoc/java.se"
			)
		doclet.windowTitle = "Jenetics ${project.version}"
		doclet.docTitle = "<h1>Jenetics ${project.version}</h1>"
		doclet.bottom = "&copy; ${Env.COPYRIGHT_YEAR} Franz Wilhelmst&ouml;tter  &nbsp;<i>(${Env.BUILD_TIME})</i>"
		doclet.stylesheetFile = project.file("${project.rootDir}/buildSrc/resources/javadoc/stylesheet.css")

		doclet.addStringOption("noqualifier", "io.jenetics.internal.collection")
		doclet.tags = listOf(
				"apiNote:a:API Note:",
				"implSpec:a:Implementation Requirements:",
				"implNote:a:Implementation Note:"
			)

		doclet.group("Core API", "io.jeneics", "io.jenetics.engine")
		doclet.group("Utilities", "io.jenetics.util", "io.jenetics.stat")

		doLast {
			copySpec {
				from("src/main/java") {
					include("io/**/doc-files/*.*")
				}
				includeEmptyDirs = false
				into(destinationDir!!)
			}
		}
	}

	val javadoc = project.tasks.findByName("javadoc") as Javadoc?
	if (javadoc != null) {
		project.tasks.register<ColorizerTask>("colorizer") {
			directory = javadoc.destinationDir!!
		}

		project.tasks.register("java2html") {
			doLast {
				project.javaexec {
					main = "de.java2html.Java2Html"
					args = listOf(
						"-srcdir", "src/main/java",
						"-targetdir", "${javadoc.destinationDir}/src-html"
					)
					classpath = files("${project.rootDir}/buildSrc/lib/java2html.jar")
				}
			}
		}

		javadoc.doLast {
			val colorizer = project.tasks.findByName("colorizer")
			colorizer?.actions?.forEach {
				it.execute(colorizer)
			}


			val java2html = project.tasks.findByName("java2html")
			java2html?.actions?.forEach {
				it.execute(java2html)
			}
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
	val identifier = "${Jenetics.NAME}-${Jenetics.VERSION}"

	from("build/package/${identifier}") {
		into(identifier)
	}

	archiveBaseName.set(identifier)
	archiveVersion.set(Jenetics.VERSION)

	doLast {
		val zip = file("${identifier}.zip")
		zip.renameTo(file("build/package${zip.name}"))
	}
}
