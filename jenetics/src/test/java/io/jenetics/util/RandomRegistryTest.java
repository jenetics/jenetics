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

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static io.jenetics.util.RandomRegistry.using;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;
import java.util.random.RandomGenerator.StreamableGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class RandomRegistryTest {

	@Test
	public void getDefault() {
		assertThat(RandomRegistry.random()).isNotNull();
	}

	@Test
	public void setDefault() {
		RandomRegistry.reset();
		final var devault = RandomRegistry.random();
		assertThat(devault).isNotNull();

		RandomRegistry.random(new Random());
		assertThat(devault).isNotSameAs(RandomRegistry.random());
		RandomRegistry.reset();

		assertThat(RandomRegistry.random()).isSameAs(devault);
	}

	@Test(invocationCount = 10)
	public void setRandom() throws Exception {
		final var devault = RandomRegistry.random();

		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			for (int i = 0; i < 25; ++i) {
				scope.fork(() -> {
					final var random = new Random();
					RandomRegistry.using(random, r -> {
						assertThat(r).isSameAs(random);

						assertThat(RandomRegistry.random()).isNotSameAs(devault);

						final var innerDefault = RandomRegistry.random();

						final Random random2 = new Random();
						RandomRegistry.random(random2);
						assertThat(RandomRegistry.random()).isSameAs(random2);

						RandomRegistry.reset();
						assertThat(RandomRegistry.random()).isNotSameAs(innerDefault);
					});

					return "";
				});
			}

			scope.join();
			scope.throwIfFailed();
		}

		assertThat(RandomRegistry.random()).isSameAs(devault);
	}


	@Test
	public void setRandomFactory() throws InterruptedException {
		final var factory = RandomGeneratorFactory.of("L128X1024MixRandom");
		RandomRegistry.random(factory);
		final var devault = RandomRegistry.random();

		for (int i = 0; i < 10; ++i) {
			assertThat(devault).isSameAs(RandomRegistry.random());
		}

		final var exception = new AtomicReference<AssertionError>();
		System.out.println(Thread.currentThread().getName());
		final var thread = new Thread(() -> {
			System.out.println(Thread.currentThread().getName());
			assertThat(devault).isNotSameAs(RandomRegistry.random());
		});
		thread.setUncaughtExceptionHandler((_, e) -> exception.set((AssertionError)e));
		thread.start();
		thread.join();

		if (exception.get() != null) {
			throw exception.get();
		}
	}

	@Test
	public void setRandomSupplier() throws InterruptedException {
		final Supplier<RandomGenerator> supplier =
			RandomGeneratorFactory.of("L128X1024MixRandom")::create;

		RandomRegistry.random(supplier);

		final var random = RandomRegistry.random();
		for (int i = 0; i < 10; ++i) {
			assertThat(random).isSameAs(RandomRegistry.random());
		}

		final var thread = new Thread(() ->
			assertThat(random).isNotSameAs(RandomRegistry.random())
		);
		thread.start();
		thread.join();
	}

	@Test(invocationCount = 10)
	public void setRandomSupplierStream() throws InterruptedException {
		final Iterator<RandomGenerator> randoms = StreamableGenerator.of("L128X1024MixRandom")
			.rngs()
			//.peek(System.out::println)
			.iterator();

		final Supplier<RandomGenerator> supplier = randoms::next;
		RandomRegistry.random(supplier);

		final var random = RandomRegistry.random();
		for (int i = 0; i < 10; ++i) {
			assertThat(random).isSameAs(RandomRegistry.random());
		}

		final var thread = new Thread(() ->
			assertThat(random).isNotSameAs(RandomRegistry.random())
		);
		thread.start();
		thread.join();
	}

	@Test(invocationCount = 10)
	public void setRandomThreading()
		throws ExecutionException, InterruptedException
	{
		final Random random = new Random();
		RandomRegistry.random(random);

		try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			final var futures = IntStream.range(0, 500)
				.mapToObj(_ -> executor
					.submit(() -> assertThat(RandomRegistry.random()).isSameAs(random)))
				.toList();

			for (Future<?> future : futures) {
				future.get();
			}
		}
	}

	@Test
	public void setThreadLocalRandom() {
		final Random random = new Random();
		RandomRegistry.random(random);

		Assert.assertSame(RandomRegistry.random(), random);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void setNullRandom() {
		RandomRegistry.random((Random)null);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void setNullTLRandom() {
		RandomRegistry.random((RandomGeneratorFactory<?>) null);
	}

	@Test
	public void localContext() {
		final var random = RandomRegistry.random();

		final Random random1 = new Random();
		using(random1, _ -> {
			final Random random2 = new Random();
			using(random2, _ -> assertThat(RandomRegistry.random()).isSameAs(random2));
			assertThat(RandomRegistry.random()).isSameAs(random1);
		});

		assertThat(RandomRegistry.random()).isSameAs(random);
	}

	@Test(invocationCount = 10)
	public void concurrentLocalContext() throws Exception {
		try (var c = Executors.newVirtualThreadPerTaskExecutor()) {
			for (int i = 0; i < 25; ++i) {
				c.execute(new ContextRunnable());
			}
		}
	}

	private static final class ContextRunnable implements Runnable {
		@Override
		public void run() {
			using(new Random(), r -> {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				assertThat(r).isSameAs(RandomRegistry.random());

				final Random random2 = new Random();
				using(random2, r2 -> {
					assertThat(RandomRegistry.random()).isSameAs(random2);
					assertThat(r2).isSameAs(random2);

					final Random random2_2 = new Random();
					RandomRegistry.random(random2_2);
					assertThat(RandomRegistry.random()).isSameAs(random2_2);

					final Random random3 = new Random();
					using(random3, r3 -> {
						assertThat(RandomRegistry.random()).isSameAs(random3);
						assertThat(r3).isSameAs(random3);
					});

					assertThat(RandomRegistry.random()).isSameAs(random2_2);
					Assert.assertNotEquals(r, RandomRegistry.random());
				});

				assertThat(r).isSameAs(RandomRegistry.random());
			});
		}
	}

	@Test
	public void withScope() {
		final List<Genotype<DoubleGene>> genotypes1 =
			RandomRegistry.with(new Random(123), _ ->
				Genotype.of(DoubleChromosome.of(0, 10)).instances()
					.limit(100)
					.collect(toList())
			);
		final List<Genotype<DoubleGene>> genotypes2 =
			RandomRegistry.with(new Random(123), _ ->
				Genotype.of(DoubleChromosome.of(0, 10)).instances()
					.limit(100)
					.collect(toList())
			);

		assertThat(genotypes1).isEqualTo(genotypes2);
	}

	//@Test
	public void defaultRandomGenerator() {
		System.setProperty(
			"io.jenetics.util.defaultRandomGenerator",
			"L64X1024MixRandom")
		;

		final var generator = RandomRegistry.random();
		assertThat(generator.getClass().getSimpleName())
			.isEqualTo("L64X1024MixRandom");
	}

}
