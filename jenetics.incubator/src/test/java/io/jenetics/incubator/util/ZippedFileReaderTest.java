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
package io.jenetics.incubator.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.ext.util.CsvSupport;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ZippedFileReaderTest {

	private static final File ZIP =
		new File("src/test/resources/io/jenetics/incubator/util/csv.zip");

	@Test(dataProvider = "files")
	public void read(final Path path) throws IOException {
		try (var reader = new ZippedFileReader(ZIP, path)) {
			final var content = CsvSupport.lines(reader).findFirst().orElseThrow();
			final var expected = "%s,1".formatted(path.getFileName());

			assertThat(content).isEqualTo(expected);
		}
	}

	@DataProvider
	public Object[][] files() {
		return new Object[][] {
			{ Path.of("csv_root.txt") },
			{ Path.of("a/csv_a.txt") },
			{ Path.of("a/c/csv_c.txt") },
			{ Path.of("a/c/d/csv_d.txt") },
			{ Path.of("b/csv_b.txt") }
		};
	}

	@Test
	public void readUnknownFile() {
		final var path = Path.of("some/not/existing/file");

		assertThatNoException()
			.isThrownBy(() -> {
				try (var reader = new ZippedFileReader(ZIP, path)) {
					assertThat(reader).isNotNull();
				}
			});

		assertThatExceptionOfType(FileNotFoundException.class)
			.isThrownBy(() -> {
				try (var reader = new ZippedFileReader(ZIP, path)) {
					assertThat(reader.read()).isEqualTo(0);
				}
			})
			.withMessage(
				"Zip entry not found: 'src/test/resources/io/jenetics/incubator" +
					"/util/csv.zip:some/not/existing/file'."
			);
	}

}
