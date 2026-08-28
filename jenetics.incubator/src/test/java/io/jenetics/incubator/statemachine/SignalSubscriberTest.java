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
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.ACTIVE;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.INACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;

import org.testng.annotations.Test;

public class SignalSubscriberTest {

	@Test
	public void requestOneItemOnSubscribeAndAfterEachItem() {
		final var transitions =
			new ArrayList<Fsm.Transition<FsmTest.ProcessState, FsmTest.Command>>();
		final var subscription = new TestSubscription();
		final var subscriber =
			new SignalSubscriber<FsmTest.ProcessState, FsmTest.Command>(
				transitions::add
			);
		final var transition = transition();

		subscriber.onSubscribe(subscription);
		subscriber.onNext(transition);

		assertThat(transitions).containsExactly(transition);
		assertThat(subscription.requests()).containsExactly(1L, 1L);
		assertThat(subscription.cancelled()).isFalse();
	}

	@Test
	public void cancelAdditionalSubscriptions() {
		final var first = new TestSubscription();
		final var second = new TestSubscription();
		final var subscriber =
			new SignalSubscriber<FsmTest.ProcessState, FsmTest.Command>(_ -> {});

		subscriber.onSubscribe(first);
		subscriber.onSubscribe(second);
		subscriber.onNext(transition());

		assertThat(first.requests()).containsExactly(1L, 1L);
		assertThat(first.cancelled()).isFalse();
		assertThat(second.requests()).isEmpty();
		assertThat(second.cancelled()).isTrue();
	}

	@Test
	public void cancelSubscriptionWhenConsumerFails() {
		final var subscription = new TestSubscription();
		final var exception = new IllegalStateException("failed");
		final var subscriber =
			new SignalSubscriber<FsmTest.ProcessState, FsmTest.Command>(
				_ -> { throw exception; }
			);

		subscriber.onSubscribe(subscription);

		assertThatExceptionOfType(IllegalStateException.class)
			.isThrownBy(() -> subscriber.onNext(transition()))
			.isSameAs(exception);
		assertThat(subscription.requests()).containsExactly(1L);
		assertThat(subscription.cancelled()).isTrue();
	}

	@Test
	public void cancelSubscriptionOnError() {
		final var subscription = new TestSubscription();
		final var subscriber =
			new SignalSubscriber<FsmTest.ProcessState, FsmTest.Command>(_ -> {});

		subscriber.onSubscribe(subscription);
		subscriber.onError(new IllegalStateException("failed"));

		assertThat(subscription.cancelled()).isTrue();
	}

	@Test
	public void rejectNullInputs() {
		final var subscriber =
			new SignalSubscriber<FsmTest.ProcessState, FsmTest.Command>(_ -> {});

		assertThatNullPointerException()
			.isThrownBy(() -> new SignalSubscriber<>(null));
		assertThatNullPointerException()
			.isThrownBy(() -> subscriber.onSubscribe(null));
		assertThatNullPointerException()
			.isThrownBy(() -> subscriber.onNext(null));
		assertThatNullPointerException()
			.isThrownBy(() -> subscriber.onError(null));
	}

	private static Fsm.Transition<FsmTest.ProcessState, FsmTest.Command>
	transition() {
		return new Fsm.Transition<>(INACTIVE, BEGIN, ACTIVE);
	}

	private static final class TestSubscription implements Flow.Subscription {
		private final List<Long> requests = new ArrayList<>();
		private boolean cancelled;

		@Override
		public void request(final long n) {
			requests.add(n);
		}

		@Override
		public void cancel() {
			cancelled = true;
		}

		List<Long> requests() {
			return requests;
		}

		boolean cancelled() {
			return cancelled;
		}
	}

}
