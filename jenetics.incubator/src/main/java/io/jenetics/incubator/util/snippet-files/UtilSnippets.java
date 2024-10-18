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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import io.jenetics.ext.util.CsvSupport;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
final class UtilSnippets {

	static class ZippedFileReaderSnippets {

		void reader() throws IOException {
			// @start region="ZippedFileReaderSnippets.creation"
			// The actual ZIP file.
			final var zip = new File("path/to/csv.zip");

			// The path to the CSV within the ZIP.
			final var path = Path.of("data/Addresses.csv");

			try (var reader = new ZippedFileReader(zip, path, UTF_8)) {
				final List<String[]> rows = CsvSupport.readAllRows(reader);
				// ... process the rows.
			}
			// @end
		}

	}

}
