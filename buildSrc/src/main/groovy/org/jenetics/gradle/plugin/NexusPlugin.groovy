/*
* Copyright 2012 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.jenetics.gradle.plugin

import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.maven.MavenDeployment
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.tasks.Upload
import org.gradle.api.tasks.bundling.Jar
import org.gradle.plugins.signing.SigningPlugin

/**
 * Original code from
 *
 * Benjamin Muschko: https://github.com/bmuschko/gradle-nexus-plugin
 */
class NexusPlugin implements Plugin<Project> {
	static final String USERNAME = 'nexus_username'
	static final String PASSWORD = 'nexus_password'

    private NexusPluginExtension nexus

	@Override
	void apply(final Project project) {
		project.plugins.apply(MavenPlugin)
		project.plugins.apply(SigningPlugin)

		nexus = project.extensions.create('nexus', NexusPluginExtension)

		applyTasks(project)
		applySigning(project)
		applyPom(project)
		applyUpload(project)
	}

	private void applyTasks(final Project project) {
		project.afterEvaluate {
			configureInstallTask(project)
			applySourcesJarTask(project)
			applyTestsJarTask(project)
		    applyJavadocJarTask(project)
		}
	}

	private void configureInstallTask(final Project project) {
		if (!nexus.usesStandardConfiguration()) {
			def task = project.tasks.getByName(MavenPlugin.INSTALL_TASK_NAME)
            task.configuration = project.configurations[nexus.configuration]
		}
	}

	private void applySourcesJarTask(final Project project) {
		if (nexus.attachSources) {
			final Jar task = project.task('sourcesJar', type: Jar) {
				classifier = 'sources'
				from project.sourceSets.main.allSource
				filter(ReplaceTokens, tokens: [
					__identifier__: nexus.identifier,
					__year__: nexus.copyrightYear
				])
			}

			project.artifacts.add(nexus.configuration, task)
		}
	}

	private void applyTestsJarTask(final Project project) {
		if (nexus.attachTests) {
			final Jar task = project.task('testsJar', type: Jar) {
				classifier = 'tests'
				from project.sourceSets.test.output
				filter(ReplaceTokens, tokens: [
					__identifier__: nexus.identifier,
					__year__: nexus.copyrightYear
				])
			}

			project.artifacts.add(nexus.configuration, task)
		}
	}

	private void applyJavadocJarTask(final Project project) {
		if (nexus.attachJavadoc) {
			final Jar task = project.task('javadocJar', type: Jar) {
				classifier = 'javadoc'
				filter(ReplaceTokens, tokens: [
					__identifier__: nexus.identifier,
					__year__: nexus.copyrightYear
				])
			}

            task.from project.javadoc
			project.artifacts.add(nexus.configuration, task)
		}
	}

	private void applySigning(final Project project) {
		project.afterEvaluate {
			if (nexus.sign) {
				project.signing {
					required {
						project.gradle.taskGraph
                                .hasTask(nexus.getUploadTaskPath(project)) &&
                       !project.version.endsWith('SNAPSHOT')
					}

					sign project.configurations[nexus.configuration]

					project.gradle.taskGraph.whenReady {
						signPom(project)
						signInstallPom(project)
					}
				}
			}
		}
	}

	private void signPom(final Project project) {
		def tasks = project.tasks.withType(Upload).matching {
			it.path == nexus.getUploadTaskPath(project)
		}

		tasks.each { task ->
			task.repositories.mavenDeployer() {
				beforeDeployment { MavenDeployment deployment ->
					project.signing.signPom(deployment)
				}
			}
		}
	}

	private void signInstallPom(final Project project) {
		def installTasks = project.tasks.withType(Upload).matching {
			it.path == nexus.getInstallTaskPath(project)
		}

		installTasks.each { task ->
			task.repositories.mavenInstaller() {
				beforeDeployment { MavenDeployment deployment ->
					project.signing.signPom(deployment)
				}
			}
		}
	}

	private void applyPom(Project project) {
		project.ext.modifyPom = { Closure modification ->
			project.afterEvaluate {
				project.poms.each {
					it.whenConfigured { project.configure(it, modification) }
				}
			}
		}

		project.afterEvaluate {
			project.ext.poms = [
                project.tasks.getByName(MavenPlugin.INSTALL_TASK_NAME).repositories.mavenInstaller(),
			    project.tasks.getByName(nexus.uploadTaskName).repositories.mavenDeployer()
            ]*.pom
		}
	}

	private void applyUpload(final Project project) {
		project.afterEvaluate {
			project.tasks.getByName(nexus.uploadTaskName).repositories.mavenDeployer() {
				project.gradle.taskGraph.whenReady { TaskExecutionGraph taskGraph ->
					if (taskGraph.hasTask(nexus.getUploadTaskPath(project))) {
						Console console = System.console()

						final String username =  project.property(USERNAME)
						final String password =  project.property(PASSWORD)

						if (nexus.repository) {
							repository(url: nexus.repository) {
								authentication(userName: username, password: password)
							}
						}

						if (nexus.snapshotRepository) {
							snapshotRepository(url: nexus.snapshotRepository) {
								authentication(userName: username, password: password)
							}
						}
					}
				}
			}
		}
	}
}

class NexusPluginExtension {
    String repository = 'https://oss.sonatype.org/service/local/staging/deploy/maven2/'
    String snapshotRepository = 'https://oss.sonatype.org/content/repositories/snapshots/'
    String configuration = Dependency.ARCHIVES_CONFIGURATION

	String identifier = 'jenetics'
	String copyrightYear = '2007-2014'

	Boolean attachSources = true
	Boolean attachTests = false
	Boolean attachJavadoc = true
    Boolean sign = true

	def nexus(Closure closure) {
		closure.delegate = this
		closure()
	}

	String getUploadTaskName() {
		"upload${configuration.capitalize()}"
	}

	String getUploadTaskPath(final Project project) {
		isRootProject(project) ? ":$uploadTaskName" : "$project.path:$uploadTaskName"
	}

	String getInstallTaskPath(final Project project) {
		isRootProject(project) ? ":$MavenPlugin.INSTALL_TASK_NAME" :
                "$project.path:$MavenPlugin.INSTALL_TASK_NAME"
	}

	private boolean isRootProject(final Project project) {
		project.rootProject == project
	}

	void setConfiguration(config) {
		configuration = config instanceof Configuration ? config.name : config
	}

	boolean usesStandardConfiguration() {
		configuration == Dependency.ARCHIVES_CONFIGURATION
	}
}
