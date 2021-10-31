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
 * @version 6.3
 */
plugins {
	base
	id("me.champeau.jmh") version "0.6.6" apply false
}

rootProject.version = Jenetics.VERSION

tasks.named<Wrapper>("wrapper") {
	gradleVersion = "7.2"
	distributionType = Wrapper.DistributionType.ALL
}

/**
 * Project configuration *before* the projects has been evaluated.
 */
allprojects {
	group =  Jenetics.GROUP
	version = Jenetics.VERSION

	repositories {
		flatDir {
			dirs("${rootDir}/buildSrc/lib")
		}
		mavenLocal()
		mavenCentral()
	}

	configurations.all {
		resolutionStrategy.preferProjectModules()
	}

}

apply("./gradle/alljavadoc.gradle")

/**
 * Project configuration *after* the projects has been evaluated.
 */
gradle.projectsEvaluated {
	subprojects {
		val project = this

		val xlint = listOf(
			"preview",
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

		tasks.withType<JavaCompile> {
			options.compilerArgs.add("-Xlint:$xlint")
		}

		tasks.withType<Test> {
			useTestNG()
		}

		plugins.withType<JavaPlugin> {
			configure<JavaPluginExtension> {
				sourceCompatibility = JavaVersion.VERSION_17
				targetCompatibility = JavaVersion.current()
			}

			setupJava(project)
			setupTestReporting(project)
			setupJavadoc(project, "")
		}

		if (plugins.hasPlugin("maven-publish")) {
			setupPublishing(project)
		}
	}

	setupJavadoc(rootProject, "all")
}

/**
 * Some common Java setup.
 */
fun setupJava(project: Project) {
	val attr = mutableMapOf(
		"Implementation-Title" to project.name,
		"Implementation-Version" to Jenetics.VERSION,
		"Implementation-URL" to Jenetics.URL,
		"Implementation-Vendor" to Jenetics.NAME,
		"ProjectName" to Jenetics.NAME,
		"Version" to Jenetics.VERSION,
		"Maintainer" to Jenetics.AUTHOR,
		"Project" to project.name,
		"Project-Version" to project.version,

		"Created-With" to "Gradle ${gradle.gradleVersion}",
		"Built-By" to Env.BUILD_BY,
		"Build-Date" to Env.BUILD_DATE,
		"Build-JDK" to Env.BUILD_JDK,
		"Build-OS-Name" to Env.BUILD_OS_NAME,
		"Build-OS-Arch" to Env.BUILD_OS_ARCH,
		"Build-OS-Version" to Env.BUILD_OS_VERSION
	)
	if (project.extra.has("moduleName")) {
		attr["Automatic-Module-Name"] = project.extra["moduleName"].toString()
	}

	project.tasks.withType<Jar> {
		manifest {
			attributes(attr)
		}
	}
}

/**
 * Setup of the Java test-environment and reporting.
 */
fun setupTestReporting(project: Project) {
	project.apply(plugin = "jacoco")

	project.configure<JacocoPluginExtension> {
		toolVersion = "0.8.7"
	}

	project.tasks {
		named<JacocoReport>("jacocoTestReport") {
			dependsOn("test")

			reports {
				html.required.set(true)
				xml.required.set(true)
				csv.required.set(true)
			}
		}

		named<Test>("test") {
			finalizedBy("jacocoTestReport")
		}
	}
}

/**
 * Setup of the projects Javadoc.
 */
fun setupJavadoc(project: Project, taskName: String) {
	project.tasks.withType<Javadoc> {
		val doclet = options as StandardJavadocDocletOptions
		doclet.addBooleanOption("Xdoclint:accessibility,html,reference,syntax", true)

		exclude("**/internal/**")

		doclet.memberLevel = JavadocMemberLevel.PROTECTED
		doclet.version(true)
		doclet.docEncoding = "UTF-8"
		doclet.charSet = "UTF-8"
		doclet.linkSource(true)
		doclet.linksOffline(
				"https://docs.oracle.com/en/java/javase/17/docs/api/",
				"${project.rootDir}/buildSrc/resources/javadoc/java.se"
			)
		doclet.windowTitle = "Jenetics ${project.version}"
		doclet.docTitle = "<h1>Jenetics ${project.version}</h1>"
		doclet.bottom = "&copy; ${Env.COPYRIGHT_YEAR} Franz Wilhelmst&ouml;tter  &nbsp;<i>(${Env.BUILD_DATE})</i>"
		doclet.stylesheetFile = project.file("${project.rootDir}/buildSrc/resources/javadoc/stylesheet.css")

		doclet.addStringOption("noqualifier", "io.jenetics.internal.collection")
		doclet.tags = listOf(
				"apiNote:a:API Note:",
				"implSpec:a:Implementation Requirements:",
				"implNote:a:Implementation Note:"
			)

		doclet.group("Core API", "io.jenetics", "io.jenetics.engine")
		doclet.group("Utilities", "io.jenetics.util", "io.jenetics.stat")

		doLast {
			project.copy {
				from("src/main/java") {
					include("io/**/doc-files/*.*")
				}
				includeEmptyDirs = false
				into(destinationDir!!)
			}
		}
	}

	val javadoc = project.tasks.findByName("${taskName}javadoc") as Javadoc?
	if (javadoc != null) {
		project.tasks.register<io.jenetics.gradle.ColorizerTask>("${taskName}colorizer") {
			directory = javadoc.destinationDir!!
		}

		project.tasks.register("${taskName}java2html") {
			doLast {
				val srcdir = file("${project.projectDir}/src/main/java")

				if (srcdir.isDirectory) {
					project.javaexec {
						mainClass.set("de.java2html.Java2Html")
						args = listOf(
							"-srcdir", srcdir.toString(),
							"-targetdir", "${javadoc.destinationDir}/src-html"
						)
						classpath = files("${project.rootDir}/buildSrc/lib/java2html.jar")
					}
				}
			}
		}

		javadoc.doLast {
			val colorizer = project.tasks.findByName("${taskName}colorizer")
			colorizer?.actions?.forEach {
				it.execute(colorizer)
			}

			val java2html = project.tasks.findByName("${taskName}java2html")
			java2html?.actions?.forEach {
				it.execute(java2html)
			}
		}
	}
}

/**
 * The Java compiler XLint flags.
 */
fun xlint(): String {
	// See https://docs.oracle.com/en/java/javase/17/docs/specs/man/javac.html
	return listOf(
		"cast",
		"classfile",
		"dep-ann",
		"deprecation",
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

val identifier = "${Jenetics.ID}-${Jenetics.VERSION}"

/**
 * Setup of the Maven publishing.
 */
fun setupPublishing(project: Project) {
	project.configure<JavaPluginExtension> {
		withJavadocJar()
		withSourcesJar()
	}

	project.tasks.named<Jar>("sourcesJar") {
		filter(
			ReplaceTokens::class, "tokens" to mapOf(
				"__identifier__" to identifier,
				"__year__" to Env.COPYRIGHT_YEAR
			)
		)
	}

	project.tasks.named<Jar>("javadocJar") {
		filter(
			ReplaceTokens::class, "tokens" to mapOf(
				"__identifier__" to identifier,
				"__year__" to Env.COPYRIGHT_YEAR
			)
		)
	}

	project.configure<PublishingExtension> {
		publications {
			create<MavenPublication>("mavenJava") {
				artifactId = project.name
				from(project.components["java"])
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
					description.set(project.description)
					url.set(Jenetics.URL)
					inceptionYear.set("2007")

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
						connection.set(Maven.SCM_CONNECTION)
						developerConnection.set(Maven.DEVELOPER_CONNECTION)
						url.set(Maven.SCM_URL)
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

	project.apply(plugin = "signing")

	project.configure<SigningExtension> {
		sign(project.the<PublishingExtension>().publications["mavenJava"])
	}
}

val exportDir = file("${rootProject.buildDir}/package/${identifier}")

val assemblePkg = "assemblePkg"
tasks.register(assemblePkg) {
	val task = this
	subprojects { task.dependsOn(tasks.build) }

	group ="archive"
	description = "Create the project package"

	doLast {
		val modules = arrayOf(
			"jenetics",
			"jenetics.ext",
			"jenetics.prog",
			"jenetics.xml"
		)

		exportDir.deleteRecursively()

		// Copy the project code.
		copy {
			from(".") {
				exclude(
					"**/build",
					"**/out",
					"**/.idea",
					"**/.settings",
					"**/.gradle",
					"**/.git"
				)
			}
			into("${exportDir}/project")
		}

		// Copy external JAR files.
		allprojects {
			val project = this

			plugins.withType<JavaPlugin> {
				configurations.all {
					if (isCanBeResolved) {
						resolvedConfiguration.files.forEach {
							if (it.name.endsWith(".jar") &&
								!it.name.startsWith("jenetics"))
							{
								project.copy {
									from(it)
									into("${exportDir}/project/buildSrc/lib")
								}
							}
						}
					}
				}
			}
		}

		// Copy the JAR files.
		copy {
			from(*modules)
			into("${exportDir}/libs")
			include("**/build/libs/*.jar")

			includeEmptyDirs = false

			eachFile {
				relativePath = RelativePath(true, name)
			}
		}

		modules.forEach { copyJavadoc(it, exportDir) }
		copyAllJavadoc(exportDir)
		modules.forEach { copyTestReports(it, exportDir) }

		// Copy the User's Manual.
		copy {
			from("jenetics.doc/build/doc") {
				include("*.pdf")
			}
			into(exportDir)
		}
	}
}

tasks.named(assemblePkg) {
	dependsOn("build", "alljavadoc")
}

fun copyJavadoc(name: String, exportDir: File) {
	copy {
		from("${name}/build/docs/javadoc") {
			filter(
				ReplaceTokens::class, "tokens" to mapOf(
					"__identifier__" to identifier,
					"__year__" to Env.COPYRIGHT_YEAR
				)
			)
		}
		into("${exportDir}/javadoc/${name}")
	}
}

fun copyAllJavadoc(exportDir: File) {
	copy {
		from("${rootDir}/build/docs/alljavadoc") {
			filter(
				ReplaceTokens::class, "tokens" to mapOf(
					"__identifier__" to identifier,
					"__year__" to Env.COPYRIGHT_YEAR
				)
			)
		}
		into("${exportDir}/javadoc/combined")
	}
}

fun copyTestReports(name: String, exportDir: File) {
	copy {
		from("${name}/build/reports") {
			exclude("**/*.gif")
			filter(
				ReplaceTokens::class, "tokens" to mapOf(
					"__identifier__" to identifier,
					"__year__" to Env.COPYRIGHT_YEAR
				)
			)
		}
		into("${exportDir}/reports/${name}")
	}
	copy {
		from("${name}/build/reports") {
			include("**/*.gif")
		}
		into("${exportDir}/reports/${name}")
	}
}

val pkgZip = "pkgZip"
tasks.register<Zip>(pkgZip) {
	dependsOn(assemblePkg)

	group ="archive"
	description = "Zips the project package"

	archiveFileName.set("${identifier}.zip")
	destinationDirectory.set(file("${rootProject.buildDir}/package"))

	from(exportDir)
}


