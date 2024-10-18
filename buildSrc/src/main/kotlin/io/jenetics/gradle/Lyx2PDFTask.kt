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
package io.jenetics.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * This tasks converts a lyx document into a PDF.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 6.1
 */
open class Lyx2PDFTask : DefaultTask() {

	@get:InputFile
	var document: File? = null

	@TaskAction
	fun lyx2PDF() {
		if (lyxExists()) {
			convert()
		} else {
			logger.lifecycle("Binary '{}' not found.", BINARY)
			logger.lifecycle("Manual PDF has not been created.")
		}
	}

	private fun convert() {
		val workingDir = document?.parentFile
		val documentName = document?.name

		val builder = ProcessBuilder(
			BINARY, "-e", "pdf2", documentName
		)
		builder.directory(workingDir)
		builder.redirectErrorStream(true)
		logger.debug("{}/{}", workingDir, documentName)

		try {
			val process = builder.start()
			output(process.inputStream)
			val exitValue = process.waitFor()

			if (exitValue != 0) {
				logger.lifecycle("Error while generating PDF.")
				logger.lifecycle("Manual PDF has not been created.")
			}
		} catch (e: Exception) {
			throw TaskExecutionException(this, e)
		}
	}

	private fun output(input: InputStream) {
		val d = BufferedReader(InputStreamReader(input))
		var line: String? = d.readLine()
		while (line != null) {
			logger.info(line)
			line = d.readLine()
		}
	}


	companion object {
		private const val BINARY = "lyx"

		private fun lyxExists(): Boolean {
			val builder = ProcessBuilder(BINARY, "-version")

			return try {
				val process = builder.start()
				process.waitFor() == 0
			} catch (e: IOException) {
				false
			} catch (e: InterruptedException) {
				Thread.currentThread().interrupt()
				return false
			}
		}
	}

}
