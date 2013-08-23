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
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import org.jenetics.colorizer.Colorizer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4 &mdash; <em>$Date$</em>
 */
public class JavadocColorizeTask extends DefaultTask {

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

			getLogger().info(
				"Colorizer processed {0} files and modified {1}",
				colorizer.getProcessed(), colorizer.getModified()
			);
		} catch (final IOException e) {
			throw new TaskExecutionException(this, e);
		}
	}

}


