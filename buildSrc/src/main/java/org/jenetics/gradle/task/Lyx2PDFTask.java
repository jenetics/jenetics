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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.gradle.task;

import java.io.File;
import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4 &mdash; <em>$Date$</em>
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

		try {
			final Process process = builder.start();
			_exitValue = process.waitFor();
			if (_exitValue != 0) {
				getLogger().lifecycle("Error while generating PDF.");
				getLogger().lifecycle("Manual PDF has not been created.");
			}
		} catch (IOException | InterruptedException e) {
			throw new TaskExecutionException(this, e);
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





