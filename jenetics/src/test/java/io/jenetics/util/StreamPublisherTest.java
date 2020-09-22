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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Problem;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class StreamPublisherTest {

	private final Problem<Integer, IntegerGene, Integer> _problem = Problem.of(
		a -> a,
		Codec.of(
			Genotype.of(IntegerChromosome.of(0, 1000)),
			g -> g.gene().allele()
		)
	);

	private final Engine<IntegerGene, Integer> _engine = Engine
		.builder(_problem)
		.build();


	@Test
	public void creation() throws InterruptedException {
		final var limit = 33;
		final var latch = new CountDownLatch(limit);
		final var running = new AtomicBoolean(true);
		final var generation = new AtomicLong();


		final Stream<Long> stream = _engine.stream()
			.limit(limit)
			.map(EvolutionResult::generation);

		try (var publisher = new StreamPublisher<Long>()) {
			publisher.subscribe(new Subscriber<>() {
				private Subscription _subscription;
				@Override
				public void onSubscribe(final Subscription subscription) {
					_subscription = subscription;
					_subscription.request(1);
				}
				@Override
				public void onNext(final Long g) {
					generation.set(g);
					latch.countDown();
					_subscription.request(1);
				}
				@Override
				public void onError(final Throwable throwable) {
				}
				@Override
				public void onComplete() {
					running.set(false);
				}
			});

			publisher.attach(stream);
			latch.await(10, TimeUnit.SECONDS);
		}

		Assert.assertEquals(generation.get(), limit);
	}

	@Test
	public void publishLimitedStream() throws InterruptedException {
		final int generations = 20;
		final var publisher = new StreamPublisher<EvolutionResult<IntegerGene, Integer>>();
		final var stream = _engine.stream().limit(generations);

		final var count = new AtomicInteger();
		final var completed = new AtomicBoolean();
		final var latch = new CountDownLatch(generations + 1);

		try (publisher) {
			publisher.subscribe(new Subscriber<>() {
				private Subscription _subscription;
				@Override
				public void onSubscribe(final Subscription subscription) {
					_subscription = requireNonNull(subscription);
					_subscription.request(1);
				}
				@Override
				public void onNext(final EvolutionResult<IntegerGene, Integer> er) {
					count.incrementAndGet();
					latch.countDown();
					_subscription.request(1);
				}
				@Override
				public void onComplete() {
					completed.set(true);
					latch.countDown();
				}
				@Override
				public void onError(final Throwable throwable) {}
			});

			publisher.attach(stream);
			latch.await(10, TimeUnit.SECONDS);
		}

		Assert.assertTrue(
			count.get() >= generations,
			format("%s < %s", count, generations)
		);
		Assert.assertTrue(completed.get());
	}

	@Test
	public void publishClosingPublisher() throws InterruptedException {
		final int generations = 20;
		final var publisher = new StreamPublisher<EvolutionResult<IntegerGene, Integer>>();
		final var stream = _engine.stream();

		final var count = new AtomicInteger();
		final var completed = new AtomicBoolean();
		final var latch = new CountDownLatch(generations + 1);

		try (publisher) {
			publisher.subscribe(new Subscriber<>() {
				private Subscription _subscription;
				@Override
				public void onSubscribe(final Subscription subscription) {
					_subscription = requireNonNull(subscription);
					_subscription.request(1);
				}
				@Override
				public void onNext(final EvolutionResult<IntegerGene, Integer> er) {
					count.incrementAndGet();
					latch.countDown();
					_subscription.request(1);
				}
				@Override
				public void onComplete() {
					completed.set(true);
					latch.countDown();
				}
				@Override
				public void onError(final Throwable throwable) {}
			});

			publisher.attach(stream);
			latch.await(10, TimeUnit.SECONDS);
		}

		Assert.assertTrue(
			count.get() >= generations,
			format("%s < %s", count, generations)
		);
		Assert.assertTrue(completed.get());
	}

}
