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
package org.jenetics.random;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class KISS32RandomCompatibilityTest {

	@Test(dataProvider = "data")
	public void random(final TestData data) {
		final String[] parameters = data.getParameters();
		final long seed = Long.parseLong(parameters[0]);

		final KISS32Random random = new KISS32Random(seed);

		for (final String[] value : data) {
			final int expected = Integer.parseInt(value[0]);
			Assert.assertEquals(random.nextInt(), expected);
		}
	}

	@Test(dataProvider = "data")
	public void threadSafeRandom(final TestData data) {
		final String[] parameters = data.getParameters();
		final long seed = Long.parseLong(parameters[0]);

		final KISS32Random random = new KISS32Random.ThreadSafe(seed);

		for (final String[] value : data) {
			final int expected = Integer.parseInt(value[0]);
			Assert.assertEquals(random.nextInt(), expected);
		}
	}

	@DataProvider(name = "data")
	public Object[][] data() {
		return TestData.list("/org/jenetics/random/KISS32Random")
			.map(data -> new Object[]{data})
			.toArray(Object[][]::new);
	}

	public static void main(final String[] args) throws IOException {
		final String dir = "org.jenetics.random/src/test/resources/" +
			"org/jenetics/random/KISS32Random";

		for (int i = 0; i < 20; ++i) {
			final long seed = i*12345678;
			final Random random = new KISS32Random(seed);

			final File file = new File(dir, format("random[%d].dat", seed));
			file.getParentFile().mkdirs();

			try (PrintWriter writer = new PrintWriter(file)) {
				for (int j = 0; j < 150; ++j) {
					writer.println(random.nextInt());
				}
			}
		}
	}

}
