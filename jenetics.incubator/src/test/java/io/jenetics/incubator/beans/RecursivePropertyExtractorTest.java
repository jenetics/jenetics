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
package io.jenetics.incubator.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import io.jenetics.incubator.beans.property.IndexProperty;
import io.jenetics.incubator.beans.property.Mutable;
import io.jenetics.incubator.beans.property.Properties;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPX.Reader;

public class RecursivePropertyExtractorTest {

	private record Data(
		Integer[][] ints,
		List<List<String>> strings
	) {}


	@Test
	public void extractRecursive() {
		final var data = new Data(
			new Integer[][] {
				{1}
			},
			listOf(
				listOf("1", "2"),
				listOf("a", "b", "c")
			)
		);

		Properties.walk(data)
			.peek(p -> {
				if (p instanceof IndexProperty ip) {
					if (ip.value().type() == String.class) {
						if (ip.value() instanceof Mutable mv) {
							var value = ip.value().value().toString();
							mv.write("A:" + value);
						}
					}
				}
			})
			.forEach(System.out::println);

		System.out.println(data);
	}

	@SafeVarargs
	private static <T> List<T> listOf(T... values) {
		return new ArrayList<T>(Arrays.asList(values));
	}

	@Test
	public void extract() throws IOException {
		final GPX gpx = Reader.DEFAULT.read(
			RecursivePropertyExtractorTest.class
				.getResourceAsStream("/Austria.gpx")
		);

		Properties.walk(new PathValue<>(gpx), "io.jenetics.*")
			.forEach(System.out::println);
	}

}
