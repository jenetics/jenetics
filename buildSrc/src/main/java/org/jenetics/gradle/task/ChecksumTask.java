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

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4 &mdash; <em>$Date: 2013-08-25 $</em>
 */
public class ChecksumTask extends DefaultTask {

	private String _algorithm = "MD5";
	private File _inputFile;
	private File _checksumFile;

	@Optional
	@Input
	public String getAlgorithm() {
		return _algorithm;
	}

	public void setAlgorithm(final String algorithm) {
		_algorithm = algorithm;
	}

	@InputFile
	public File getInputFile() {
		return _inputFile;
	}

	public void setInputFile(final File inputFile) {
		_inputFile = inputFile;
	}

	@Optional
	@OutputFile
	public File getChecksumFile() {
		File checksumFile = _checksumFile;

		if (checksumFile == null) {
			final String extension = _algorithm.toLowerCase().replace("-", "");
			checksumFile = new File(
				_inputFile.getParent(),
				format("%s.%s", _inputFile.getName(), extension)
			);
		}

		return checksumFile;
	}

	public void setChecksumFile(final File checksumFile) {
		_checksumFile = checksumFile;
	}


	@TaskAction
	public void checksum() {
		try {
			final MessageDigest digest = MessageDigest.getInstance(_algorithm);

			// Read the file.
			try (FileInputStream in = new FileInputStream(_inputFile)) {
				final byte[] data = new byte[4096];
				int length = 0;
				while ((length = in.read(data)) != -1) {
					digest.update(data, 0, length);
				}
			}

			// Write the checksum file.
			try (FileOutputStream out = new FileOutputStream(getChecksumFile())) {
				out.write(toString(digest.digest()).getBytes());
			}
		} catch (NoSuchAlgorithmException | IOException e) {
			throw new TaskExecutionException(this, e);
		}
	}

	private static String toString(final byte[] checksum) {
		return new BigInteger(1, checksum).toString(16) + "\n";
	}

}


