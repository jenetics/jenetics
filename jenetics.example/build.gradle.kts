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
	packaging
}

val moduleName = "io.jenetics.example"

dependencies {
	implementation(project(":jenetics"))
	implementation(project(":jenetics.ext"))
	implementation(project(":jenetics.prog"))
	implementation(Libs.JPX)
	implementation(Libs.PRNGine)
	implementation(Libs.RX_JAVA)

	testImplementation(Libs.TestNG)
}

tasks.jar {
	manifest {
		attributes("Automatic-Module-Name" to moduleName)
	}
}

packaging {
	name = Jenetics.NAME
	author = Jenetics.AUTHOR
	url = Jenetics.URL
	jarjar = false
	javadoc = false

	doLast {
		copy {
			from("src/main/scripts") {
				include("**/*")
			}
			into(packaging.ext["exportScriptDir"]!!)
			filter(
				ReplaceTokens::class, "tokens" to mapOf(
					"__version__" to project.version,
					"__identifier__" to "${Jenetics.NAME}-${Jenetics.VERSION}",
					"__year__" to Env.COPYRIGHT_YEAR
				)
			)
		}
	}
}


