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
package io.jenetics.internal.collection;

import static java.lang.String.format;
import static io.jenetics.internal.math.random.nextASCIIString;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.util.IO;
import io.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class SerializationTest {

	private static final String RESOURCE_PATTERN =
		"/io/jenetics/collection/serialization/%s[%s].object";

	@Test
	public void serialization() throws IOException {
		for (int i = 0; i < 10; ++i) {
			final Random random = new Random(i);
			final MSeq<String> seq = MSeq.ofLength(i);
			seq.fill(() -> nextASCIIString(100, random));

			String resource = format(RESOURCE_PATTERN, "MSeq", i);
			try (InputStream in = getClass().getResourceAsStream(resource)) {
				final Object o = IO.object.read(in);
				Assert.assertEquals(o, seq);
			}

			resource = format(RESOURCE_PATTERN, "ISeq", i);
			try (InputStream in = getClass().getResourceAsStream(resource)) {
				final Object o = IO.object.read(in);
				Assert.assertEquals(o, seq.toISeq());
			}
		}
	}

	public static void main(final String[] args) throws IOException {
		final File baseDir = new File(
			"jenetics/src/test/resources/io/jenetics/collection/serialization");
		if (!baseDir.isDirectory() && !baseDir.mkdirs()) {
			throw new IOException("Error while creating directory " + baseDir);
		}

		for (int i = 0; i < 10; ++i) {
			final Random random = new Random(i);
			final MSeq<String> seq = MSeq.ofLength(i);
			seq.fill(() -> nextASCIIString(100, random));

			IO.object.write(seq, new File(baseDir, format("MSeq[%s].object", i)));
			IO.object.write(seq.toISeq(), new File(baseDir, format("ISeq[%s].object", i)));
		}
	}

}
