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

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
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
		final var lock = new ReentrantLock();
		final var finished = lock.newCondition();
		final var running = new AtomicBoolean(true);
		final var generation = new AtomicLong();


		final Stream<Long> stream = _engine.stream()
			.limit(33)
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
					_subscription.request(1);
				}
				@Override
				public void onError(final Throwable throwable) {
				}
				@Override
				public void onComplete() {
					lock.lock();
					try {
						running.set(false);
						finished.signal();
					} finally {
						lock.unlock();
					}
				}
			});

			publisher.attach(stream);

			lock.lock();
			try {
				while (running.get()) {
					finished.await();
				}
			} finally {
				lock.unlock();
			}
		}

		Assert.assertEquals(generation.get(), 33);
	}

	@Test
	public void publishLimitedStream() throws InterruptedException {
		final int generations = 20;
		final var publisher = new StreamPublisher<EvolutionResult<IntegerGene, Integer>>();
		final var stream = _engine.stream().limit(generations);

		final var lock = new ReentrantLock();
		final var finished = lock.newCondition();
		final AtomicBoolean running = new AtomicBoolean(true);
		final AtomicBoolean completed = new AtomicBoolean(false);

		final AtomicInteger count = new AtomicInteger();
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
				_subscription.request(1);
			}
			@Override
			public void onComplete() {
				lock.lock();
				try {
					running.set(false);
					completed.set(true);
					finished.signal();
				} finally {
					lock.unlock();
				}
			}
			@Override
			public void onError(final Throwable throwable) {}
		});

		publisher.attach(stream);

		lock.lock();
		try {
			while (running.get()) {
				finished.await();
			}
		} finally {
			lock.unlock();
		}

		publisher.close();

		Assert.assertEquals(count.get(), generations);
		Assert.assertTrue(completed.get());
	}

	@Test
	public void publishClosingPublisher() throws InterruptedException {
		final int generations = 20;
		final var publisher = new StreamPublisher<EvolutionResult<IntegerGene, Integer>>();
		final var stream = _engine.stream();

		final var lock = new ReentrantLock();
		final var finished = lock.newCondition();
		final AtomicBoolean running = new AtomicBoolean(true);
		final AtomicBoolean completed = new AtomicBoolean(false);

		final AtomicInteger count = new AtomicInteger();
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
				lock.lock();
				try {
					running.set(er.generation() < generations);
					finished.signal();
				} finally {
					lock.unlock();
				}
				_subscription.request(1);
			}
			@Override
			public void onComplete() {
				lock.lock();
				try {
					completed.set(true);
					finished.signalAll();
				} finally {
					lock.unlock();
				}
			}
			@Override
			public void onError(final Throwable throwable) {}
		});

		publisher.attach(stream);

		lock.lock();
		try {
			while (running.get()) {
				finished.await();
			}
		} finally {
			lock.unlock();
		}

		publisher.close();

		lock.lock();
		try {
			while (!completed.get()) {
				finished.await();
			}
		} finally {
			lock.unlock();
		}

		Assert.assertEquals(count.get(), generations);
		Assert.assertTrue(completed.get());
	}

}
