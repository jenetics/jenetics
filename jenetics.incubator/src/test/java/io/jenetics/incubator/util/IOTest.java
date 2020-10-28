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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class IOTest {

	private static final List<Object> OBJECTS = List.of(
		"asdf",
		"asfasdf",
		1L,
		3,
		5.4,
		5345.445F
	);

	@Test
	public void readWrite() throws IOException {
		final var tempFile = Files.createTempFile("IO", "TEST");
		System.out.println(tempFile);
		try {
			IO.write(OBJECTS, tempFile);
			try (var objects = IO.read(tempFile)) {
				Assert.assertEquals(
					OBJECTS,
					objects.collect(Collectors.toList())
				);
			}
		} finally {
			Files.deleteIfExists(tempFile);
		}
	}

}
