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
package org.jenetics.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-01-30 $</em>
 * @since @__version__@
 */
public class RandRegistry {

	private static Entry entry = new Entry(ThreadLocalRandom.current());

	static Random getRandom() {
		return entry.random;
	}

	static void setRandom(final Random random) {
		entry.random = random;
	}

	static Context<Random> with(final Random random) {
		entry = entry.inner(random);
		return new Ctx(random);
	}

	private static final class Ctx implements Context<Random> {
		final Random _random;

		public Ctx(Random random) {
			_random = random;
		}

		@Override
		public Random get() {
			return _random;
		}

		@Override
		public void close() {
			entry = entry.parent;
		}
	}

	private static final class Entry {
		Random random;
		Entry parent;

		Entry(final Random random, final Entry parent) {
			this.random = random;
			this.parent = parent;
		}

		Entry(final Random random) {
			this(random, null);
		}

		Entry inner(final Random random) {
			return new Entry(random, this);
		}
	}

}
