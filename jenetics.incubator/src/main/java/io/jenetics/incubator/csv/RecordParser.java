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
package io.jenetics.incubator.csv;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.function.Function;

import io.jenetics.ext.util.CsvSupport;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class RecordParser {

	record Rec(String first, String last, String date) {
		/*
		Rec(String... row) {
			this(row[0], row[1], row[2]);
		}
		 */
	}

	<R extends Record> Function<String[], R> to(Class<R> type) {
		final RecordComponent[] components = type.getRecordComponents();
		final Constructor<R> constructor;
		try {
			constructor= type.getDeclaredConstructor(
				Arrays.stream(components)
					.map(RecordComponent::getType)
					.toArray(Class[]::new)
			);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

		return row -> {
			try {
				return constructor.newInstance((Object[])row);
			} catch (InstantiationException |
			         IllegalAccessException | InvocationTargetException e)
			{
				throw new RuntimeException(e);
			}
		};
	}

	public void parseToRecord() throws IOException {
		final var rsc = "/io/jenetics/ext/util/customers-100.csv";

		final var reader = new CsvSupport.LineReader();
		final var splitter = new CsvSupport.LineSplitter(new CsvSupport.ColumnIndexes(3, 2, 10));

		try (var in = getClass().getResourceAsStream(rsc);
		     var isr = new InputStreamReader(in))
		{
			reader.read(isr)
				.skip(1)
				.map(splitter::split)
				.map(to(Rec.class))
				.forEach(System.out::println);
		}
	}

}
