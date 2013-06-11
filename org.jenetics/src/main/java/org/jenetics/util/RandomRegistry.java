/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import static java.util.Objects.requireNonNull;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javolution.context.LocalContext;
import javolution.lang.Reference;

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
 * With the help of the {@link LocalContext} from the <a href="http://javolution.org/">
 * javolution</a> project you can temporarily (and locally) change the
 * implementation of the PRNG
 *
 * [code]
 * public class GA {
 *     public static void main(final String[] args) {
 *         ...
 *         final GeneticAlgorithm<Float64Gene, Float64> ga = ...
 *
 *         LocalContext.enter();
 *         try {
 *             RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadSafe(1234));
 *             // Only the 'setup' step uses the new PRGN.
 *             ga.setup();
 *         } finally {
 *             LocalContext.exit(); // Restore the previous random engine.
 *         }
 *
 *         ga.evolve(100);
 *     }
 * }
 * [/code]
 * <p/>
 *
 * @see LocalContext
 * @see Random
 * @see ThreadLocalRandom
 * @see LCG64ShiftRandom
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.2 &mdash; <em>$Date$</em>
 */
public final class RandomRegistry extends StaticObject {
	private RandomRegistry() {}

	private static final Reference<Random> TLOCAL_REF = new Ref<Random>() {
		@Override
		public Random get() {
			return ThreadLocalRandom.current();
		}
	};

	private static final LocalContext.Reference<Reference<? extends Random>>
	RANDOM = new LocalContext.Reference<Reference<? extends Random>>(TLOCAL_REF);

	/**
	 * Return the global {@link Random} object.
	 *
	 * @return the global {@link Random} object.
	 */
	public static Random getRandom() {
		return RANDOM.get().get();
	}

	/**
	 * Set the new global {@link Random} object for the GA. The given
	 * {@link Random} <b>must</b> be thread safe, which is the case for the
	 * default Java {@code Random} implementation.
	 * <p/>
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
		RANDOM.set(new RRef(random));
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
	public static void setRandom(final ThreadLocal<? extends Random> random) {
		RANDOM.set(new TLRRef<>(random));
	}

	/**
	 * Set the random object to it's default value. The <i>default</i> used PRNG
	 * is the {@link ThreadLocalRandom} PRNG.
	 */
	public static void reset() {
		RANDOM.set(TLOCAL_REF);
	}


	/*
	 * Some helper Reference classes.
	 */

	private static abstract class Ref<R> implements Reference<R> {
		@Override public void set(final R random) {}
	}

	private final static class RRef extends Ref<Random> {
		private final Random _random;
		public RRef(final Random random) {
			_random = requireNonNull(random, "Random");
		}
		@Override public Random get() {
			return _random;
		}
	}

	private final static class TLRRef<R extends Random> extends Ref<R> {
		private final ThreadLocal<R> _random;
		public TLRRef(final ThreadLocal<R> random) {
			_random = requireNonNull(random, "Random");
		}
		@Override public R get() {
			return _random.get();
		}
	}

}





