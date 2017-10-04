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

import java.io.File;
import java.io.IOException;
import java.util.Random;

import io.jenetics.util.IO;
import io.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class SerializationTest {


	public static void main(final String[] args) throws IOException {
		final File baseDir = new File("jenetics/src/test/resources/io/jenetics/collection/serialization");
		if (!baseDir.isDirectory() && !baseDir.mkdirs()) {
			throw new IOException("Error while creating directory " + baseDir);
		}

		for (int i = 0; i < 10; ++i) {
			final Random random = new Random(i);
			final MSeq<Integer> seq = MSeq.ofLength(i);
			seq.fill(random::nextInt);

			IO.object.write(seq, new File(baseDir, format("MSeq[%s].dat", i)));
			IO.object.write(seq.toISeq(), new File(baseDir, format("ISeq[%s].dat", i)));
		}
	}


}
