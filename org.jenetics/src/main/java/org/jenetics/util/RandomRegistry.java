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

import static org.jenetics.util.object.nonNull;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javolution.context.LocalContext;
import javolution.lang.Reference;

/**
 * This class holds the {@link Random} engine used for the GA. The
 * {@code RandomRegistry} is thread safe. The default value for the random
 * engine is an instance of the Java {@link Random} engine with the
 * {@link System#currentTimeMillis()} as seed value.
 * <p/>
 * You can temporarily (and locally) change the implementation of the random engine
 * by using the {@link LocalContext} from the
 * <a href="http://javolution.org/">javolution</a> project.
 *
 * [code]
 * LocalContext.enter();
 * try {
 *     RandomRegistry.setRandom(new MyRandom());
 *     ...
 * } finally {
 *     LocalContext.exit(); // Restore the previous random engine.
 * }
 * [/code]
 * <p/>
 * The used <i>default</i> PRNG is the {@link ThreadLocalRandom} object.
 *
 * @see LocalContext
 * @see ThreadLocalRandom
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2012-11-16 $</em>
 */
public final class RandomRegistry {

	private static final Reference<Random> THREAD_LOCAL_REF = new Ref<Random>() {
		@Override public Random get() {
			return ThreadLocalRandom.current();
		}
	};

	private static final LocalContext.Reference<Reference<? extends Random>>
	RANDOM = new LocalContext.Reference<Reference<? extends Random>>(THREAD_LOCAL_REF);


	private RandomRegistry() {
		throw new AssertionError("Don't create an 'RandomRegistry' instance.");
	}

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
	 * {@link Random} <b>must</b> be thread-safe, which is the case for the
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
	 * {@link Random} don't have be thread-safe, because the given
	 * {@link ThreadLocal} wrapper guarantees thread-safety. Setting a
	 * <i>thread-local</i> random object leads, in general, to a faster
	 * PRN generation, when using a non-blocking PRNG.
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
		RANDOM.set(THREAD_LOCAL_REF);
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
			nonNull(random, "Random");
			_random = random;
		}
		@Override public Random get() {
			return _random;
		}
	}

	private final static class TLRRef<R extends Random> extends Ref<R> {
		private final ThreadLocal<R> _random;
		public TLRRef(final ThreadLocal<R> random) {
			nonNull(random, "Random");
			_random = random;
		}
		@Override public R get() {
			return _random.get();
		}
	}

}





