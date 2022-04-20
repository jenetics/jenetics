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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static io.jenetics.util.RandomRegistry.using;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
import io.jenetics.internal.util.Concurrency;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class RandomRegistryTest {

	@Test
	public void setDefault() {
		RandomRegistry.reset();
		final var devault = RandomRegistry.random();
		Assert.assertNotNull(devault);

		RandomRegistry.random(new Random());
		Assert.assertNotNull(RandomRegistry.random());
		RandomRegistry.reset();

		assertSame(RandomRegistry.random(), devault);
	}

	@Test
	public void setRandom() {
		final Random random = new Random();
		RandomRegistry.random(random);

		assertSame(RandomRegistry.random(), random);
	}

	@Test
	public void setRandomFactory() throws InterruptedException {
		final var factory = RandomGeneratorFactory.of("L128X1024MixRandom");
		RandomRegistry.random(factory);

		final var random = RandomRegistry.random();
		for (int i = 0; i < 10; ++i) {
			assertThat(random).isSameAs(RandomRegistry.random());
		}

		final var exception = new AtomicReference<AssertionError>();
		final var thread = new Thread(() ->
			assertThat(random).isNotSameAs(RandomRegistry.random())
		);
		thread.setUncaughtExceptionHandler((t, e) -> exception.set((AssertionError)e));
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

	@Test
	public void setRandomSupplierStream() throws InterruptedException {
		final Iterator<RandomGenerator> randoms = StreamableGenerator.of("L128X1024MixRandom")
			.rngs()
			.peek(System.out::println)
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

	@Test
	public void setRandomThreading()
		throws ExecutionException, InterruptedException
	{
		final Random random = new Random();
		RandomRegistry.random(random);

		final ExecutorService executor = Executors.newFixedThreadPool(10);
		try {
			final var futures = IntStream.range(0, 500)
				.mapToObj(i -> executor
					.submit(() -> assertSame(RandomRegistry.random(), random)))
				.toList();

			for (Future<?> future : futures) {
				future.get();
			}
		} finally {
			executor.shutdown();
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
		using(random1, r1 -> {
			final Random random2 = new Random();
			using(random2, r2 -> assertSame(RandomRegistry.random(), random2));
			assertSame(RandomRegistry.random(), random1);
		});

		assertSame(RandomRegistry.random(), random);
	}

	@Test(invocationCount = 10)
	public void concurrentLocalContext() {
		try (Concurrency c = Concurrency.withCommonPool()) {
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
				assertSame(r, RandomRegistry.random());

				final Random random2 = new Random();
				using(random2, r2 -> {
					assertSame(RandomRegistry.random(), random2);
					assertSame(r2, random2);

					final Random random2_2 = new Random();
					RandomRegistry.random(random2_2);
					assertSame(RandomRegistry.random(), random2_2);

					final Random random3 = new Random();
					using(random3, r3 -> {
						assertSame(RandomRegistry.random(), random3);
						assertSame(r3, random3);
					});

					assertSame(RandomRegistry.random(), random2_2);
					Assert.assertNotEquals(r, RandomRegistry.random());
				});

				assertSame(r, RandomRegistry.random());
			});
		}
	}

	@Test
	public void withScope() {
		final List<Genotype<DoubleGene>> genotypes1 =
			RandomRegistry.with(new Random(123), random ->
				Genotype.of(DoubleChromosome.of(0, 10)).instances()
					.limit(100)
					.collect(toList())
			);
		final List<Genotype<DoubleGene>> genotypes2 =
			RandomRegistry.with(new Random(123), random ->
				Genotype.of(DoubleChromosome.of(0, 10)).instances()
					.limit(100)
					.collect(toList())
			);

		assertEquals(genotypes1, genotypes2);
	}

}
