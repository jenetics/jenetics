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
package io.jenetics.util;

import static java.util.Objects.requireNonNull;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * This class holds the {@link RandomGenerator} engine used for the GA. The
 * {@code RandomRegistry} is thread safe and is initialized with the
 * {@link RandomGeneratorFactory#getDefault()} PRNG.
 *
 * <h2>Setup the PRNG used for the evolution process</h2>
 * There are several ways on how to set the {@link RandomGenerator} used during
 * the evolution process.
 * <p>
 *
 * <b>Using a {@link RandomGeneratorFactory}</b><br>
 * The following example registers the <em>L128X1024MixRandom</em> random
 * generator. By using a factory, each threads gets its own generator instance,
 * which ensures thread-safety without the necessity of the created random
 * generator to be thread-safe.
 * <pre>{@code
 * // This is the default setup.
 * RandomRegistry.random(RandomGeneratorFactory.getDefault());
 *
 * // Using the "L128X1024MixRandom" random generator for the evolution.
 * RandomRegistry.random(RandomGeneratorFactory.of("L128X1024MixRandom"));
 * }</pre>
 * <br>
 *
 * <b>Using a {@link RandomGenerator} {@link Supplier}</b><br>
 * If you have a random engine, which is not available as
 * {@link RandomGeneratorFactory}, it is also possible to register a
 * {@link Supplier} of the desired random generator. This method has the same
 * thread-safety property as the method above.
 * <pre>{@code
 * RandomRegistry.random(() -> new MySpecialRandomGenerator());
 * }</pre>
 *
 * Register a random generator supplier is also more flexible. It allows to
 * use the streaming and splitting capabilities of the random generators
 * implemented in the Java library.
 * <pre>{@code
 * final Iterator<RandomGenerator> randoms =
 *     StreamableGenerator.of("L128X1024MixRandom")
 *         .rngs()
 *         .iterator();
 *
 * RandomRegistry.random(randoms::next);
 * }</pre>
 * <br>
 *
 * <b>Using a {@link RandomGenerator} instance</b><br>
 * It is also possible to set a single random generator instance for the whole
 * evolution process. When using this setup, the used random generator must be
 * thread safe.
 * <pre>{@code
 * RandomRegistry.random(new Random(123456));
 * }</pre>
 * <p>
 *
 * The following code snippet shows an almost complete example of a typical
 * random generator setup.
 * <pre>{@code
 * public class GA {
 *     public static void main(final String[] args) {
 *         // Initialize the registry with the factory of the PRGN.
 *         final var factory = RandomGeneratorFactory.of("L128X1024MixRandom");
 *         RandomRegistry.random(factory);
 *
 *         final Engine<DoubleGene, Double> engine = ...;
 *         final EvolutionResult<DoubleGene, Double> result = engine.stream()
 *             .limit(100)
 *             .collect(toBestEvolutionResult());
 *     }
 * }
 * }</pre>
 *
 * <h2>Setup of a <i>local</i> PRNG</h2>
 *
 * You can temporarily (and locally) change the implementation of the PRNG. E.g.
 * for initialize the engine stream with the same initial population.
 *
 * <pre>{@code
 * public class GA {
 *     public static void main(final String[] args) {
 *         // Create a reproducible list of genotypes.
 *         final var factory = RandomGeneratorFactory.of("L128X1024MixRandom");
 *         final List<Genotype<DoubleGene>> genotypes =
 *             with(factory.create(123), r ->
 *                 Genotype.of(DoubleChromosome.of(0, 10)).instances()
 *                     .limit(50)
 *                     .collect(toList())
 *             );
 *
 *         final Engine<DoubleGene, Double> engine = ...;
 *         final EvolutionResult<DoubleGene, Double> result = engine
 *              // Initialize the evolution stream with the given genotypes.
 *             .stream(genotypes)
 *             .limit(100)
 *             .collect(toBestEvolutionResult());
 *     }
 * }
 * }</pre>
 *
 * @see RandomGenerator
 * @see RandomGeneratorFactory
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version !__version__!
 */
public final class RandomRegistry {
	private RandomRegistry() {}

	/**
	 * Thread local wrapper for a random generator supplier (factory).
	 *
	 * @param <R> the type of the random generator
	 */
	private static final class TLR<R extends RandomGenerator>
		extends ThreadLocal<R>
		implements Supplier<R>
	{
		private final Supplier<? extends R> _factory;

		TLR(final Supplier<? extends R> factory) {
			_factory = requireNonNull(factory);
		}

		@Override
		protected synchronized R initialValue() {
			return _factory.get();
		}
	}

	private static final TLR<RandomGenerator> DEFAULT_RANDOM_FACTORY =
		new TLR<>(RandomGeneratorFactory.getDefault()::create);

	private static final Context<Supplier<? extends RandomGenerator>> CONTEXT =
		new Context<>(DEFAULT_RANDOM_FACTORY);

	/**
	 * Return the global {@link RandomGenerator} object.
	 *
	 * @return the global {@link RandomGenerator} object.
	 */
	public static RandomGenerator random() {
		return CONTEXT.get().get();
	}

	/**
	 * Set the new global {@link RandomGenerator} object for the GA. The given
	 * {@link RandomGenerator} <b>must</b> be thread safe, which is the case for the
	 * default Java {@code RandomGenerator} implementation.
	 * <p>
	 * Setting a <i>thread-local</i> random object leads, in general, to a faster
	 * PRN generation, because the given {@code Random} engine don't have to be
	 * thread-safe.
	 *
	 * @see #random(RandomGeneratorFactory)
	 *
	 * @param random the new global {@link RandomGenerator} object for the GA.
	 * @throws NullPointerException if the {@code random} object is {@code null}.
	 */
	public static void random(final RandomGenerator random) {
		requireNonNull(random);
		CONTEXT.set(() -> random);
	}

	/**
	 * Set the new global {@link Random} object for the GA. The given
	 * {@link Random} don't have been thread safe, because the given
	 * {@link ThreadLocal} wrapper guarantees thread safety. Setting a
	 * <i>thread-local</i> random object leads, in general, to a faster
	 * PRN generation, when using a non-blocking PRNG. This is the preferred
	 * way for changing the PRNG.
	 *
	 * @param factory the thread-local random engine to use.
	 * @throws NullPointerException if the {@code random} object is {@code null}.
	 */
	public static <R extends RandomGenerator> void
	random(final RandomGeneratorFactory<? extends R> factory) {
		requireNonNull(factory);
		CONTEXT.set(new TLR<>(factory::create));
	}

	public static <R extends RandomGenerator> void
	random(final Supplier<? extends R> factory) {
		requireNonNull(factory);
		CONTEXT.set(new TLR<>(factory));
	}

	/**
	 * Set the random object to it's default value. The <i>default</i> used PRNG
	 * is the {@link ThreadLocalRandom} PRNG.
	 */
	public static void reset() {
		CONTEXT.reset();
	}

	/**
	 * Executes the consumer code using the given {@code random} engine.
	 *
	 * <pre>{@code
	 * final MSeq<Integer> seq = ...
	 * using(new Random(123), r -> {
	 *     seq.shuffle();
	 * });
	 * }</pre>
	 *
	 * The example above shuffles the given integer {@code seq} <i>using</i> the
	 * given {@code Random(123)} engine.
	 *
	 * @since 3.0
	 *
	 * @param random the PRNG used within the consumer
	 * @param consumer the consumer which is executed with the <i>scope</i> of
	 *        the given {@code random} engine.
	 * @param <R> the type of the random engine
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <R extends RandomGenerator> void using(
		final R random,
		final Consumer<? super R> consumer
	) {
		CONTEXT.with(
			() -> random,
			r -> { consumer.accept(random); return null; }
		);
	}

	/**
	 * Executes the consumer code using the given {@code random} engine.
	 *
	 * <pre>{@code
	 * final MSeq<Integer> seq = ...
	 * using(new LCG64ShiftRandom.ThreadLocal(), r -> {
	 *     seq.shuffle();
	 * });
	 * }</pre>
	 *
	 * The example above shuffles the given integer {@code seq} <i>using</i> the
	 * given {@code LCG64ShiftRandom.ThreadLocal()} engine.
	 *
	 * @since !__version__!
	 *
	 * @param factory the PRNG used within the consumer
	 * @param consumer the consumer which is executed with the <i>scope</i> of
	 *        the given {@code random} engine.
	 * @param <R> the type of the random engine
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <R extends RandomGenerator> void using(
		final RandomGeneratorFactory<? extends R> factory,
		final Consumer<? super R> consumer
	) {
		CONTEXT.with(
			new TLR<>(factory::create),
			r -> { consumer.accept(r.get()); return null; }
		);
	}

	public static <R extends RandomGenerator> void using(
		final Supplier<? extends R> supplier,
		final Consumer<? super R> consumer
	) {
		CONTEXT.with(
			new TLR<>(supplier),
			r -> { consumer.accept(r.get()); return null; }
		);
	}

	/**
	 * Opens a new {@code Scope} with the given random engine and executes the
	 * given function within it. The following example shows how to create a
	 * reproducible list of genotypes:
	 * <pre>{@code
	 * final List<Genotype<DoubleGene>> genotypes =
	 *     with(new LCG64ShiftRandom(123), r ->
	 *         Genotype.of(DoubleChromosome.of(0, 10)).instances()
	 *            .limit(50)
	 *            .collect(toList())
	 *     );
	 * }</pre>
	 *
	 * @since 3.0
	 *
	 * @param <R> the type of the random engine
	 * @param <T> the function return type
	 * @param random the PRNG used for the opened scope
	 * @param function the function to apply within the random scope
	 * @return the object returned by the given function
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <R extends RandomGenerator, T> T with(
		final R random,
		final Function<? super R, ? extends T> function
	) {
		return CONTEXT.with(
			() -> random,
			s -> function.apply(random)
		);
	}

	/**
	 * Opens a new {@code Scope} with the given random engine and executes the
	 * given function within it. The following example shows how to create a
	 * reproducible list of genotypes:
	 * <pre>{@code
	 * final List<Genotype<DoubleGene>> genotypes =
	 *     with(new LCG64ShiftRandom.ThreadLocal(), random ->
	 *         Genotype.of(DoubleChromosome.of(0, 10)).instances()
	 *            .limit(50)
	 *            .collect(toList())
	 *     );
	 * }</pre>
	 *
	 * @since 3.0
	 *
	 * @param <R> the type of the random engine
	 * @param <T> the function return type
	 * @param factory the PRNG used for the opened scope
	 * @param function the function to apply within the random scope
	 * @return the object returned by the given function
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static <R extends RandomGenerator, T> T with(
		final RandomGeneratorFactory<? extends R> factory,
		final Function<? super R, ? extends T> function
	) {
		return CONTEXT.with(
			new TLR<>(factory::create),
			r -> function.apply(r.get())
		);
	}

	public static <R extends RandomGenerator, T> T with(
		final Supplier<? extends R> supplier,
		final Function<? super R, ? extends T> function
	) {
		return CONTEXT.with(
			new TLR<>(supplier),
			r -> function.apply(r.get())
		);
	}

}
