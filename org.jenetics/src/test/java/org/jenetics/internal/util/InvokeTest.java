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
package org.jenetics.internal.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class InvokeTest {

	private static class TestCloseable implements Closeable {

		boolean closed = false;

		@Override
		public void close() throws IOException {
			closed = true;
			throw new IOException("Error");
		}
	}

	@Test
	public void all() throws Exception {
		final TestCloseable[] objects = IntStream.rangeClosed(0, 10)
			.mapToObj(i -> new TestCloseable())
			.toArray(TestCloseable[]::new);

		try {
			Invoke.all(TestCloseable::close, objects);
			Assert.assertTrue(false, "Close method must throw.");
		} catch (IOException ignore) {
		}

		for (TestCloseable c : objects) {
			Assert.assertTrue(c.closed);
		}
	}

	@Test
	public void closeAll() throws Exception {
		final TestCloseable[] objects = IntStream.rangeClosed(0, 10)
			.mapToObj(i -> new TestCloseable())
			.toArray(TestCloseable[]::new);

		try {
			Invoke.closeAll(objects);
			Assert.assertTrue(false, "Close method must throw.");
		} catch (IOException ignore) {
		}

		for (TestCloseable c : objects) {
			Assert.assertTrue(c.closed);
		}
	}

}
