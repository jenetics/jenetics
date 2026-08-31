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
package io.jenetics.incubator.statemachine;

import static io.jenetics.incubator.statemachine.FsmTest.Command.BEGIN;
import static io.jenetics.incubator.statemachine.FsmTest.Command.END;
import static io.jenetics.incubator.statemachine.FsmTest.Command.EXIT;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.ACTIVE;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.INACTIVE;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.TERMINATED;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.testng.Assert.fail;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

public class SignalPublisherTest {

	private enum Signal {
		COMPLETE
	}

	@Test
	public void publishTransitionsAndCompleteAfterFinalState() {
		try (var publisher = new SignalPublisher<>(new SymbolStepper<>(FsmTest.FSM))) {
			final var subscriber =
				new RecordingSubscriber<Fsm.Transition<FsmTest.ProcessState, FsmTest.Command>>();
			publisher.subscribe(subscriber);
			subscriber.awaitSubscribed();

			publisher.submit(BEGIN);
			publisher.submit(END);
			publisher.submit(EXIT);

			subscriber.awaitComplete();

			assertThat(subscriber.items()).containsExactly(
				new Fsm.Transition<>(INACTIVE, BEGIN, ACTIVE),
				new Fsm.Transition<>(ACTIVE, END, INACTIVE),
				new Fsm.Transition<>(INACTIVE, EXIT, TERMINATED),
				Signal.COMPLETE
			);
			assertThat(subscriber.completeCount()).isOne();
			assertThat(subscriber.errors()).isEmpty();
			assertThat(publisher.isClosed()).isTrue();
		}
	}

	@Test
	public void rejectSignalsAfterFinalState() {
		try (var publisher = new SignalPublisher<>(new SymbolStepper<>(FsmTest.FSM))) {
			final var subscriber =
				new RecordingSubscriber<Fsm.Transition<FsmTest.ProcessState, FsmTest.Command>>();
			publisher.subscribe(subscriber);
			subscriber.awaitSubscribed();

			publisher.submit(EXIT);
			subscriber.awaitComplete();

			assertThatIllegalStateException()
				.isThrownBy(() -> publisher.submit(BEGIN));
		}
	}

	@Test
	public void closeExceptionallyWhenStepperRejectsSignal() {
		final var start = Fsm.State.of("start");
		final var end = Fsm.State.of("end");
		final var known = Fsm.Symbol.of("known");
		final var unknown = Fsm.Symbol.of("unknown");
		final Fsm<Fsm.State, Fsm.Symbol> fsm = new Fsm<>(
			Set.of(known),
			Set.of(start, end),
			start,
			(_, _) -> Optional.empty(),
			Set.of(end)
		);

		try (var publisher = new SignalPublisher<>(new SymbolStepper<>(fsm))) {
			final var subscriber =
				new RecordingSubscriber<Fsm.Transition<Fsm.State, Fsm.Symbol>>();
			publisher.subscribe(subscriber);
			subscriber.awaitSubscribed();

			assertThatIllegalArgumentException()
				.isThrownBy(() -> publisher.submit(unknown));
			subscriber.awaitError();

			assertThat(publisher.isClosed()).isTrue();
			assertThat(publisher.getClosedException())
				.isInstanceOf(IllegalArgumentException.class);
			assertThat(subscriber.errors())
				.hasOnlyElementsOfType(IllegalArgumentException.class);
		}
	}

	private static final class RecordingSubscriber<T>
		implements Flow.Subscriber<T>
	{
		private static final Duration TIMEOUT = Duration.ofSeconds(5);

		private final CountDownLatch subscribed = new CountDownLatch(1);
		private final CountDownLatch completed = new CountDownLatch(1);
		private final CountDownLatch errored = new CountDownLatch(1);
		private final List<Object> items = new CopyOnWriteArrayList<>();
		private final List<Throwable> errors = new CopyOnWriteArrayList<>();
		private final AtomicInteger completeCount = new AtomicInteger();

		private Flow.Subscription subscription;

		@Override
		public void onSubscribe(final Flow.Subscription subscription) {
			this.subscription = requireNonNull(subscription);
			this.subscription.request(Long.MAX_VALUE);
			subscribed.countDown();
		}

		@Override
		public void onNext(final T item) {
			items.add(item);
		}

		@Override
		public void onError(final Throwable throwable) {
			errors.add(throwable);
			errored.countDown();
		}

		@Override
		public void onComplete() {
			items.add(Signal.COMPLETE);
			completeCount.incrementAndGet();
			completed.countDown();
		}

		void awaitSubscribed() {
			await(subscribed, "subscription");
		}

		void awaitComplete() {
			await(completed, "completion");
		}

		void awaitError() {
			await(errored, "error");
		}

		List<Object> items() {
			return items;
		}

		List<Throwable> errors() {
			return errors;
		}

		int completeCount() {
			return completeCount.get();
		}

		private static void await(final CountDownLatch latch, final String event) {
			try {
				if (!latch.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)) {
					fail("Timed out waiting for " + event + ".");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				fail("Interrupted while waiting for " + event + ".", e);
			}
		}
	}

}
