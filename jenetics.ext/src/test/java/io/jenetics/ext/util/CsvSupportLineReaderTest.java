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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.stream.Stream;

import org.testng.annotations.Test;

import io.jenetics.ext.util.CsvSupport.LineReader;
import io.jenetics.ext.util.CsvSupport.Quote;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CsvSupportLineReaderTest {

	@Test
	public void read() {
		final var csv = """
			0.0,0.0000
			0.1,0.0740
			0.2,0.1120
			0.3,0.1380
			0.4,0.1760
			0.5,0.2500
			0.6,0.3840
			0.7,0.6020
			0.8,0.9280
			0.9,1.3860
			1.0,2.0000
			""";

		final var reader = new LineReader();
		try (Stream<String> lines = reader.read(CharBuffer.wrap(csv))) {
			final var count = lines.count();
			assertThat(count).isEqualTo(11);
		}
	}

	@Test
	public void readQuotedNotClosed() {
		final var csv = """
			0.0,"0.0000
			0.1,0.0740
			""";

		final var reader = new LineReader();
		try (Stream<String> lines = reader.read(CharBuffer.wrap(csv))) {
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					final var count = lines.count();
					assertThat(count).isEqualTo(0);
				})
				.withMessageContaining("Unbalanced quote character");
		}
	}

	@Test
	public void readFromStream() {
		final var in = getClass().getResourceAsStream(
			"/io/jenetics/ext/util/customers-100.csv"
		);
		final var reader = new LineReader(new Quote('"'));

		try (Stream<String> lines = reader.read( new InputStreamReader(in))) {
			assertThat(lines.count()).isEqualTo(101);
		}
	}

}
