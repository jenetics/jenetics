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

import java.io.File;
import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import org.jenetics.colorizer.Colorizer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4
 */
public class ColorizerTask extends DefaultTask {

	private File _directory;

	@Input
	public File getDirectory() {
		return _directory;
	}

	public void setDirectory(final File directory) {
		_directory = directory;
	}

	@TaskAction
	public void colorize() {
		try {
			final Colorizer colorizer = new Colorizer(_directory);
			colorizer.colorize();

			getLogger().lifecycle(
				"Colorizer processed {} files and modified {}.",
				colorizer.getProcessed(), colorizer.getModified()
			);
		} catch (final IOException e) {
			throw new TaskExecutionException(this, e);
		}
	}

}
