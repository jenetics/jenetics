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

import static java.util.Objects.requireNonNull;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import org.jenetics.internal.util.Context;

/**
 * This class holds the {@link Random} engine used for the GA. The
 * {@code RandomRegistry} is thread safe. The registry is initialized with the
 * {@link ThreadLocalRandom} PRNG, which has a much better performance behavior
 * than an instance of the {@code Random} class. Alternatively, you can
 * initialize the registry with one of the PRNG, which are being part of the
 * library.
 * <p>
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
 *         final GeneticAlgorithm&lt;DoubleGene, Double&gt; ga = ...
 *         ga.evolve(100);
 *     }
 * }
 * [/code]
 * <p>
 *
 * <b>Setup of a <i>local</i> PRNG</b><br>
 *
 * You can temporarily (and locally) change the implementation of the PRNG.
 *
 * [code]
 * public class GA {
 *     public static void main(final String[] args) {
 *         ...
 *         final GeneticAlgorithm&lt;DoubleGene, Double&gt; ga = ...
 *         final LCG64ShiftRandom random = new LCG64ShiftRandom(1234)
 *
 *         try (Scoped&lt;Random&gt; scope = RandomRegistry.scope(random)) {
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
 * <p>
 *
 * @see Random
 * @see ThreadLocalRandom
 * @see LCG64ShiftRandom
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2014-04-16 $</em>
 */
public final class RandomRegistry extends StaticObject {
	private RandomRegistry() {}

	private static final Context<Supplier<Random>> CONTEXT =
		new Context<>(ThreadLocalRandom::current);

	/**
	 * Return the global {@link Random} object.
	 *
	 * @return the global {@link Random} object.
	 */
	public static Random getRandom() {
		return CONTEXT.get().get();
	}

	/**
	 * Set the new global {@link Random} object for the GA. The given
	 * {@link Random} <b>must</b> be thread safe, which is the case for the
	 * default Java {@code Random} implementation.
	 * <p>
	 * Setting a <i>thread-local</i> random object leads, in general, to a faster
	 * PRN generation, because the given {@code Random} engine don't have to be
	 * thread-safe.
	 *
	 * @see #setRandom(ThreadLocal)
	 *
	 * @param random the new global {@link Random} object for the GA.
	 * @throws NullPointerException if the {@code random} object is {@code null}.
	 */
	public static void setRandom(final Random random) {
		requireNonNull(random, "Random must not be null.");
		CONTEXT.set(() -> random);
	}

	/**
	 * Set the new global {@link Random} object for the GA. The given
	 * {@link Random} don't have be thread safe, because the given
	 * {@link ThreadLocal} wrapper guarantees thread safety. Setting a
	 * <i>thread-local</i> random object leads, in general, to a faster
	 * PRN generation, when using a non-blocking PRNG. This is the preferred
	 * way for changing the PRNG.
	 *
	 * @param random the thread-local random engine to use.
	 * @throws NullPointerException if the {@code random} object is {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public static void setRandom(final ThreadLocal<? extends Random> random) {
		requireNonNull(random, "Random must not be null.");
		CONTEXT.set(random::get);
	}

	/**
	 * Set the random object to it's default value. The <i>default</i> used PRNG
	 * is the {@link ThreadLocalRandom} PRNG.
	 */
	public static void reset() {
		CONTEXT.reset();
	}

	/**
	 * Opens a new {@code Scope} with the given random engine.
	 *
	 * @param <R> the type of the random engine
	 * @param random the PRNG used for the opened scope.
	 * @return the scope with the given random object.
	 */
	@SuppressWarnings("unchecked")
	public static <R extends Random> Scoped<R> scope(final R random) {
		return CONTEXT.scope(() -> random, () -> random);
	}

	/**
	 * Opens a new {@code Scope} with the given random engine.
	 *
	 * @param <R> the type of the random engine
	 * @param random the PRNG used for the opened scope.
	 * @return the scope with the given random object.
	 */
	@SuppressWarnings("unchecked")
	public static <R extends Random> Scoped<R> scope(final ThreadLocal<R> random) {
		return CONTEXT.scope(random::get, random::get);
	}

}
