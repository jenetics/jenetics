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

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Flow;
import java.util.function.Consumer;

/**
 * A simple signal subscriber, which forwards published and transformed signals
 * to the wrapped transition consumer.
 * {@snippet lang=java:
 * try (var publisher = new SignalPublisher<>(new SymbolStepper<>(FSM))) {
 *     publisher.subscribe(
 *         new SignalSubscriber<>(transition -> handle(transition))
 *     );
 *
 *     publisher.submit(BEGIN);
 *     publisher.submit(END);
 *     publisher.submit(EXIT);
 * }
 * }
 *
 * @see SignalPublisher
 *
 * @param <ST> the state type
 * @param <SI> the symbol type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.1
 * @since 9.1
 */
public final class SignalSubscriber<ST extends Fsm.State, SI extends Fsm.Signal>
	implements Flow.Subscriber<Fsm.Transition<ST, SI>>
{

	private final Consumer<? super Fsm.Transition<ST, SI>> consumer;

	private Flow.Subscription subscription;
	private boolean completed;

	/**
	 * Wraps the given transition {@code consumer} into a transition subscriber.
	 *
	 * @param consumer the wrapped transition consumer
	 */
	public SignalSubscriber(Consumer<? super Fsm.Transition<ST, SI>> consumer) {
		this.consumer = requireNonNull(consumer);
	}

	@Override
	public void onSubscribe(Flow.Subscription subscription) {
		requireNonNull(subscription);

		final boolean accept;
		synchronized (this) {
			accept = this.subscription == null && !completed;
			if (accept) {
				this.subscription = subscription;
			}
		}

		if (accept) {
			subscription.request(1);
		} else {
			subscription.cancel();
		}
	}

	@Override
	public void onNext(Fsm.Transition<ST, SI> transition) {
		requireNonNull(transition);

		final var subscription = subscription();
		if (subscription == null) {
			return;
		}

		try {
			consumer.accept(transition);
		} catch (RuntimeException | Error error) {
			cancel(subscription);
			throw error;
		}

		if (isSubscribed(subscription)) {
			subscription.request(1);
		}
	}

	@Override
	public void onError(Throwable throwable) {
		requireNonNull(throwable);

		final var subscription = complete();
		if (subscription != null) {
			subscription.cancel();
		}
	}

	@Override
	public void onComplete() {
		complete();
	}

	private synchronized Flow.Subscription subscription() {
		return completed ? null : subscription;
	}

	private synchronized boolean isSubscribed(
		final Flow.Subscription subscription
	) {
		return this.subscription == subscription && !completed;
	}

	private void cancel(final Flow.Subscription subscription) {
		synchronized (this) {
			if (this.subscription == subscription) {
				this.subscription = null;
				completed = true;
			}
		}
		subscription.cancel();
	}

	private synchronized Flow.Subscription complete() {
		final var subscription = this.subscription;
		this.subscription = null;
		completed = true;
		return subscription;
	}
}
