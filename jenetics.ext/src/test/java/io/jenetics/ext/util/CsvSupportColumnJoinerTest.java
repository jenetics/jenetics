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
package io.jenetics.ext.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.testng.annotations.Test;

import io.jenetics.ext.util.CsvSupport.ColumnIndexes;
import io.jenetics.ext.util.CsvSupport.ColumnJoiner;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CsvSupportColumnJoinerTest {

	@Test
	public void embedDateToCsv() {
		final var data = new String[][] {
			{"Region", "City", "Country"},
			{"06", "aixas", "ad"},
			{"06", "aixirivali", "ad"},
			{"06", "aixirivall", "ad"},
			{"06", "aixirvall", "ad"},
			{"06", "aixovall", "ad"}
		};

		final var embedding = new ColumnIndexes(
			// Writes 'Region' as fourth column.
			3,
			// Write 'City' as second column.
			1,
			// Write 'Country' as third column.
			0,

			// Since the data rows have only three elements, the
			// missing column data are set to an empty string.
			// The last written column index will be 6, which
			// results to 7 written columns
			6
		);

		final var joiner = new ColumnJoiner(embedding);

		final var csv = Arrays.stream(data)
			.map(joiner::join)
			.collect(CsvSupport.toCsv("\n"));

		assertThat(csv).isEqualTo("""
			Country,City,,Region,,,
			ad,aixas,,06,,,
			ad,aixirivali,,06,,,
			ad,aixirivall,,06,,,
			ad,aixirvall,,06,,,
			ad,aixovall,,06,,,
			""");
	}

}
