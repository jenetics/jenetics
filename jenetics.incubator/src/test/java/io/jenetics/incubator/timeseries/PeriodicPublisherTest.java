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
package io.jenetics.incubator.timeseries;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class PeriodicPublisherTest {

	@Test
	public void publish() throws Throwable {
		final var counter = new AtomicInteger();
		final var publisher = new PeriodicPublisher<>(
			counter::incrementAndGet,
			Duration.ofMillis(50)
		);

		final var ready = new CountDownLatch(10);
		final var error = new AtomicReference<Throwable>();

		try (publisher) {
			publisher.subscribe(new Subscriber<>() {
				private Subscription _subscription;
				private int _expected = 1;

				@Override
				public void onSubscribe(final Subscription subscription) {
					(_subscription = subscription).request(1);
				}
				@Override
				public void onNext(final Integer value) {
					Assert.assertEquals(value.intValue(), _expected);

					++_expected;
					ready.countDown();
					_subscription.request(1);
				}
				@Override
				public void onError(final Throwable throwable) {
					error.set(throwable);
				}
				@Override
				public void onComplete() { }
			});

			publisher.start();
			ready.await(2, TimeUnit.SECONDS);
		}

		if (error.get() != null) {
			throw error.get();
		}
	}

	public static void main(final String[] args) throws Exception {
		final var publisher = new PeriodicPublisher<>(
			() -> ThreadLocalRandom.current().nextDouble(),
			Duration.ofMillis(1_000)
		);

		try (publisher) {
			publisher.subscribe(new Subscriber<>() {
				private Subscription _subscription;
				@Override
				public void onSubscribe(final Subscription subscription) {
					(_subscription = subscription).request(1);
				}
				@Override
				public void onNext(final Double value) {
					System.out.println("Got value: " + value);
					_subscription.request(1);
				}
				@Override
				public void onError(final Throwable throwable) {}
				@Override
				public void onComplete() {}
			});

			publisher.start();

			// Need to block, otherwise the publisher will be closed immediately.
			Thread.sleep(10_000);
		}
	}

}
