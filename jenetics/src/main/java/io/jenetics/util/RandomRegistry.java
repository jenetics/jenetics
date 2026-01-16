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

import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * This class holds the {@link RandomGenerator} engine used for the GA. The
 * {@code RandomRegistry} is thread safe and is initialized with the
 * {@link RandomGeneratorFactory#getDefault()} PRNG.
 *
 * <h2>Set up the PRNG used for the evolution process</h2>
 * There are several ways on how to set the {@link RandomGenerator} used during
 * the evolution process.
 * <p>
 *
 * <b>Using a {@link RandomGeneratorFactory}</b><br>
 * The following example registers the <em>L128X1024MixRandom</em> random
 * generator. By using a factory, each thread gets its own generator instance,
 * which ensures thread-safety without the necessity of the created random
 * generator to be thread-safe.
 * {@snippet lang="java":
 * // This is the default setup.
 * RandomRegistry.random(RandomGeneratorFactory.getDefault());
 *
 * // Using the "L128X1024MixRandom" random generator for the evolution.
 * RandomRegistry.random(RandomGeneratorFactory.of("L128X1024MixRandom"));
 * }
 * <br>
 *
 * <b>Using a {@link RandomGenerator} {@link Supplier}</b><br>
 * If you have a random engine, which is not available as
 * {@link RandomGeneratorFactory}, it is also possible to register a
 * {@link Supplier} of the desired random generator. This method has the same
 * thread-safety property as the method above.
 * {@snippet lang="java":
 * RandomRegistry.random(() -> new MySpecialRandomGenerator());
 * }
 *
 * Register a random generator supplier is also more flexible. It allows
 * using the streaming and splitting capabilities of the random generators
 * implemented in the Java library.
 * {@snippet lang="java":
 * final Iterator<RandomGenerator> randoms =
 *     StreamableGenerator.of("L128X1024MixRandom")
 *         .rngs()
 *         .iterator();
 *
 * RandomRegistry.random(randoms::next);
 * }
 * <br>
 *
 * <b>Using a {@link RandomGenerator} instance</b><br>
 * It is also possible to set a single random generator instance for the whole
 * evolution process. When using this setup, the used random generator must be
 * thread safe.
 * {@snippet lang="java":
 * RandomRegistry.random(new Random(123456));
 * }
 * <p>
 *
 * The following code snippet shows an almost complete example of a typical
 * random generator setup.
 * {@snippet lang="java":
 * public class GA {
 *     public static void main(final String[] args) {
 *         // Initialize the registry with the factory of the PRGN.
 *         final var factory = RandomGeneratorFactory.of("L128X1024MixRandom");
 *         RandomRegistry.random(factory);
 *
 *         final Engine<DoubleGene, Double> engine = null; // @replace substring='null' replacement="..."
 *         final EvolutionResult<DoubleGene, Double> result = engine.stream()
 *             .limit(100)
 *             .collect(toBestEvolutionResult());
 *     }
 * }
 * }
 *
 * <h2>Setup of a <i>local</i> PRNG</h2>
 *
 * You can temporarily (and locally) change the implementation of the PRNG. E.g.,
 * for initialize the engine stream with the same initial population.
 * {@snippet lang="java":
 * public class GA {
 *     public static void main(final String[] args) {
 *         // Create a reproducible list of genotypes.
 *         final var factory = RandomGeneratorFactory.of("L128X1024MixRandom");
 *         final List<Genotype<DoubleGene>> genotypes = RandomRegistry
 *             .with(factory.create(123))
 *             .call(() ->
 *                 Genotype.of(DoubleChromosome.of(0, 10)).instances()
 *                     .limit(50)
 *                     .collect(toList())
 *             );
 *
 *         final Engine<DoubleGene, Double> engine = null; // @replace substring='null' replacement="..."
 *         final EvolutionResult<DoubleGene, Double> result = engine
 *              // Initialize the evolution stream with the given genotypes.
 *             .stream(genotypes)
 *             .limit(100)
 *             .collect(toBestEvolutionResult());
 *     }
 * }
 * }
 *
 * <p>
 * The default random generator used by <em>Jenetics</em> is
 * {@code L64X256MixRandom}. Via the system property
 * {@code io.jenetics.util.defaultRandomGenerator}, it is possible to use a
 * different random generator.
 * <pre>{@code
 * java -Dio.jenetics.util.defaultRandomGenerator=L64X1024MixRandom \
 *      -cp jenetics-@__version__@.jar:app.jar \
 *          com.foo.bar.MyJeneticsApp
 * }</pre>
 *
 * @see RandomGenerator
 * @see RandomGeneratorFactory
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 9.0
 */
public final class RandomRegistry {

	/**
	 * Runs code with specifically bound random generator.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version 9.0
	 * @since 9.0
	 */
	public static final class Runner {

		private final ScopedVariable.Runner runner;

		private Runner(final ScopedVariable.Runner runner) {
			this.runner = requireNonNull(runner);
		}

		/**
		 * Runs an operation with each scoped value in this mapping bound to
		 * its value in the current thread.
		 *
		 * @param op the operation to run
		 */
		public void run(Runnable op) {
			runner.run(op);
		}

		/**
		 * Calls a value-returning operation with each scoped random generator.
		 *
		 * @param op the operation to run
		 * @param <R> the type of the result of the operation
		 * @param <X> type of the exception thrown by the operation
		 * @return the result
		 * @throws X if {@code op} completes with an exception
		 */
		public <R, X extends Throwable> R call(ScopedValue.CallableOp<? extends R, X> op)
			throws X
		{
			return runner.call(op);
		}

	}

	private RandomRegistry() {
	}

	private static final ThreadLocal<RandomGenerator>
		DEFAULT_RANDOM_FACTORY =
		toThreadLocal(() ->
			RandomGeneratorFactory
				.of(Env.defaultRandomGeneratorName)
				.create()
		);

	private static final ScopedVariable<ThreadLocal<RandomGenerator>>
		RANDOM =
		ScopedVariable.of(DEFAULT_RANDOM_FACTORY);

	@SuppressWarnings("unchecked")
	private static ThreadLocal<RandomGenerator>
	toThreadLocal(final Supplier<? extends RandomGenerator> factory) {
		if (factory instanceof ThreadLocal<?> tl) {
			return (ThreadLocal<RandomGenerator>)tl;
		} else {
			return ThreadLocal.withInitial(factory);
		}
	}

	/**
	 * Return the {@link RandomGenerator} of the current scope.
	 *
	 * @return the {@link RandomGenerator} of the current scope
	 */
	public static RandomGenerator random() {
		return RANDOM.get().get();
	}

	/**
	 * Set a new {@link RandomGenerator} for the <em>global</em> scope. The given
	 * {@link RandomGenerator} <b>must</b> be thread safe, which is the case for
	 * the Java {@link Random} class. Each thread will get the <em>same</em>
	 * random generator, when getting one with the {@link #random()} method.
	 *
	 * @implSpec
	 * The given {@code random} generator <b>must</b> be thread safe.
	 *
	 * @see #random(RandomGeneratorFactory)
	 *
	 * @param random the new {@link RandomGenerator} for the <em>global</em>
	 *        scope
	 * @throws NullPointerException if the {@code random} object is {@code null}
	 */
	public static void random(final RandomGenerator random) {
		requireNonNull(random);
		RANDOM.set(toThreadLocal(() -> random));
	}

	/**
	 * Set a new {@link RandomGeneratorFactory} for the <em>global</em> scope.
	 * When setting a random generator <em>factory</em> instead of the
	 * generator directly, every thread gets its own generator. It is not
	 * necessary, that the created random generators must be thread-safe.
	 *
	 * @param factory the random generator factory
	 * @throws NullPointerException if the {@code factory} object is {@code null}.
	 */
	public static void random(final RandomGeneratorFactory<?> factory) {
		requireNonNull(factory);
		RANDOM.set(toThreadLocal(factory::create));
	}

	/**
	 * Set a new {@link Supplier} of {@link RandomGenerator} for the
	 * <em>global</em> scope.
	 * When setting a random generator <em>supplier</em> instead of the
	 * generator directly, every thread gets its own generator, as returned by
	 * the supplier. It is not necessary, that the created random generators must
	 * be thread-safe.
	 *
	 * @see #random(RandomGeneratorFactory)
	 *
	 * @param supplier the random generator supplier
	 * @throws NullPointerException if the {@code supplier} object is {@code null}.
	 */
	public static void random(final Supplier<? extends RandomGenerator> supplier) {
		requireNonNull(supplier);
		RANDOM.set(toThreadLocal(supplier));
	}

	/**
	 * Set the random object to its default value.
	 */
	public static void reset() {
		RANDOM.reset();
	}


	/**
	 * Return a scoped runner with the given {@code random} generator bound to
	 * the {@link RandomRegistry}.
	 *
	 * @param random the {@code random} generator to bind
	 * @return a new scoped runner object
	 */
	public static Runner with(final RandomGenerator random) {
		requireNonNull(random);
		return new Runner(
			ScopedVariable.with(RANDOM.value(toThreadLocal(() -> random)))
		);
	}

	/**
	 * Return a scoped runner with the given random generator {@code factory}
	 * bound to the {@link RandomRegistry}. Every thread spawned in the returned
	 * runner will use a new random generator, created by the factory.
	 *
	 * @param factory the random generator factory used for creating the desired
	 *        random generator. Every thread gets its own random generator when
	 *        calling {@link RandomRegistry#random()}.
	 * @return a new scoped runner object
	 */
	public static Runner with(final RandomGeneratorFactory<?> factory) {
		requireNonNull(factory);
		return new Runner(
			ScopedVariable.with(RANDOM.value(toThreadLocal(factory::create)))
		);
	}

	/**
	 * Return a scoped runner with the given random generator {@code supplier}
	 * bound to the {@link RandomRegistry}. Every thread spawned in the returned
	 * runner will use a new random generator, returned by the supplier.
	 *
	 * @param supplier the random generator supplier used for creating the desired
	 *        random generator. Every thread gets its own random generator when
	 *        calling {@link RandomRegistry#random()}.
	 * @return a new scoped runner object
	 */
	public static Runner
	with(final Supplier<? extends RandomGenerator> supplier) {
		requireNonNull(supplier);
		return new Runner(
			ScopedVariable.with(RANDOM.value(toThreadLocal(supplier)))
		);
	}

	private record Env() {
		private static final String defaultRandomGeneratorName = get();

		private static String get() {
			return getConfigured()
				.or(Env::getDefault)
				.orElseGet(Env::getBest);
		}

		private static Optional<String> getConfigured() {
			return Optional.ofNullable(System.getProperty(
				"io.jenetics.util.defaultRandomGenerator"
			));
		}

		private static Optional<String> getDefault() {
			return RandomGeneratorFactory.all()
				.map(RandomGeneratorFactory::name)
				.filter("L64X256MixRandom"::equals)
				.findFirst();
		}

		private static String getBest() {
			final var highestStateBits = Comparator
				.<RandomGeneratorFactory<?>>comparingInt(RandomGeneratorFactory::stateBits)
				.reversed();

			return RandomGeneratorFactory.all()
				.sorted(highestStateBits)
				.map(RandomGeneratorFactory::name)
				.findFirst()
				.orElse("Random");
		}

	}

}
