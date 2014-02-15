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
 * This class holds the {@link Random} engine used for the GA. The
 * {@code RandomRegistry} is thread safe. The registry is initialized with the
 * {@link ThreadLocalRandom} PRNG, which has a much better performance behavior
 * than an instance of the {@code Random} class. Alternatively, you can
 * initialize the registry with one of the PRNG, which are being part of the
 * library.
 * <p/>
 *
 * <b>Setup of a <i>global</i> PRNG</b>
 *
 * [code]
 * public class GA {
 *     public static void main(final String[] args) {
 *         // Initialize the registry with a ThreadLocal instance of the PRGN.
 *         // This is the preferred way setting a new PRGN.
 *         RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadLocal());
 *
 *         // Using a thread safe variant of the PRGN. Leads to slower PRN
 *         // generation, but gives you the possibility to set a PRNG seed.
 *         RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadSafe(1234));
 *
 *         ...
 *         final GeneticAlgorithm<Float64Gene, Float64> ga = ...
 *         ga.evolve(100);
 *     }
 * }
 * [/code]
 * <p/>
 *
 * <b>Setup of a <i>local</i> PRNG</b><br/>
 *
 * You can temporarily (and locally) change the implementation of the PRNG.
 *
 * [code]
 * public class GA {
 *     public static void main(final String[] args) {
 *         ...
 *         final GeneticAlgorithm<Float64Gene, Float64> ga = ...
 *         final LCG64ShiftRandom random = new LCG64ShiftRandom(1234)
 *
 *         try (Scoped<Random> scope = RandomRegistry.scope(random) {
 *             // Easy access the random engine of the opened scope.
 *             assert(scope.get() == random);
 *
 *             // Only the 'setup' step uses the new PRGN.
 *             ga.setup();
 *         }
 *
 *         ga.evolve(100);
 *     }
 * }
 * [/code]
 * <p/>
 *
 * @see Random
 * @see ThreadLocalRandom
 * @see LCG64ShiftRandom
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.6 &mdash; <em>$Date: 2014-02-15 $</em>
 */
public final class RandRegistry extends StaticObject {
	private RandRegistry() {}

	private static final ThreadLocal<Entry> THREAD_LOCAL_ENTRY = new ThreadLocal<>();

	private static AtomicReference<Entry> ENTRY = new AtomicReference<>(
		new Entry(ThreadLocalRandom.current())
	);

	public static Random getRandom() {
		return getEntry().random;
	}

	public static void setRandom(final Random random) {
		setEntry(random);
	}

	private static Entry getEntry() {
		final Entry e = THREAD_LOCAL_ENTRY.get();
		return e != null ? e : ENTRY.get();
	}

	private static void setEntry(final Random random) {
		final Entry e = THREAD_LOCAL_ENTRY.get();
		if (e != null) e.random = random; else ENTRY.set(new Entry(random));
	}

	/**
	 * Opens a new {@code Scope} with the given random engine.
	 *
	 * @param random the PRNG used for the opened scope.
	 * @return the scope with the given random object.
	 */
	public static Scoped<Random> scope(final Random random) {
		final Entry e = THREAD_LOCAL_ENTRY.get();
		if (e != null) {
			THREAD_LOCAL_ENTRY.set(e.inner(random));
		} else {
			THREAD_LOCAL_ENTRY.set(new Entry(random, Thread.currentThread()));
		}

		return new RandomScope(random);
	}

	private static final class RandomScope implements Scoped<Random> {
		private final Random _random;

		public RandomScope(final Random random) {
			_random = random;
		}

		@Override
		public Random get() {
			return _random;
		}

		@Override
		public void close() {
			final Entry e = THREAD_LOCAL_ENTRY.get();
			if (e != null) {
				if (e.thread != Thread.currentThread()) {
					throw new IllegalStateException(
						"Random context must be closed by the creating thread."
					);
				}

				THREAD_LOCAL_ENTRY.set(e.parent);
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
