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
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-01-30 $</em>
 * @since @__version__@
 */
public class RandRegistry {

	private static final ThreadLocal<Entry> ENTRY = new ThreadLocal<>();

	private static AtomicReference<Entry> entry = new AtomicReference<>(
		new Entry(ThreadLocalRandom.current())
	);

	static Random getRandom() {
		return getEntry().random;
	}

	static void setRandom(final Random random) {
		setEntry(random);
	}

	private static Entry getEntry() {
		final Entry e = ENTRY.get();
		return e != null ? e : entry.get();
	}

	private static void setEntry(final Random random) {
		final Entry e = ENTRY.get();
		if (e != null) e.random = random; else entry.set(new Entry(random));
	}

	static Context<Random> with(final Random random) {
		final Entry e = ENTRY.get();
		if (e != null) {
			ENTRY.set(e.inner(random));
		} else {
			ENTRY.set(new Entry(random, Thread.currentThread()));
		}

		return new Ctx(random);
	}

	private static final class Ctx implements Context<Random> {
		private final Random _random;

		public Ctx(final Random random) {
			_random = random;
		}

		@Override
		public Random get() {
			return _random;
		}

		@Override
		public void close() {
			final Entry e = ENTRY.get();
			if (e != null) {
				if (e.thread != Thread.currentThread()) {
					throw new IllegalStateException(
						"Random context must be closed by the creating thread."
					);
				}

				ENTRY.set(e.parent);
			} else {
				throw new IllegalStateException(
					"Random context has been already close."
				);
			}
		}
	}

	private static final class Entry {
		final Thread thread;
		final Entry parent;

		Random random;

		Entry(final Random random, final Entry parent, final Thread thread) {
			this.random = random;
			this.parent = parent;
			this.thread = thread;
		}

		Entry(final Random random, final Entry parent) {
			this(random, parent, null);
		}

		Entry(final Random random, final Thread thread) {
			this(random, null, thread);
		}

		Entry(final Random random) {
			this(random, null, null);
		}

		Entry inner(final Random random) {
			assert(thread == Thread.currentThread());
			return new Entry(random, this, thread);
		}
	}

}
