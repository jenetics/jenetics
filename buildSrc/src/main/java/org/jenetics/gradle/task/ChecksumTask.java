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
 * @version 1.4
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
				for (int l = in.read(data); l != -1; l = in.read(data)) {
					digest.update(data, 0, l);
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
