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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.gradle.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

/**
 * This tasks converts a lyx document into a PDF.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4
 */
public class Lyx2PDFTask extends DefaultTask {

	private static final String BINARY = "lyx";

	private File _document;
	private Integer _exitValue;

	@InputFile
	public File getDocument() {
		return _document;
	}

	public void setDocument(final File document) {
		_document = document;
	}

	public Integer getExitValue() {
		return _exitValue;
	}

	@TaskAction
	public void lyx2PDF() {
		if (lyxExists()) {
			convert();
		} else {
			getLogger().lifecycle("Binary '{}' not found.", BINARY);
			getLogger().lifecycle("Manual PDF has not been created.");
		}
	}

	private void convert() {
		final File workingDir = _document.getParentFile();
		final String documentName = _document.getName();

		final ProcessBuilder builder = new ProcessBuilder(
			BINARY, "-e", "pdf2", documentName
		);
		builder.directory(workingDir);
		builder.redirectErrorStream(true);
		getLogger().debug(workingDir + "/" + documentName);

		try {
			final Process process = builder.start();
			output(process.getInputStream());
			_exitValue = process.waitFor();
			if (_exitValue != 0) {
				getLogger().lifecycle("Error while generating PDF.");
				getLogger().lifecycle("Manual PDF has not been created.");
			}
		} catch (IOException | InterruptedException e) {
			throw new TaskExecutionException(this, e);
		}
	}

	private void output(final InputStream in) throws IOException {
		final BufferedReader d = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = d.readLine()) != null) {
			getLogger().info(line);
		}
	}

	private static boolean lyxExists() {
		final ProcessBuilder builder = new ProcessBuilder(BINARY, "-version");

		try {
			final Process process = builder.start();
			return process.waitFor() == 0;
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}


}
