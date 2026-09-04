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

import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.BiConsumer;

/**
 * Reactive signal transition publisher. The actual state transitions are created
 * by the wrapped stepper.
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
 * @see SignalSubscriber
 *
 * @param <ST> the state type
 * @param <SI> the symbol type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.1
 * @since 9.1
 */
public class SignalPublisher<ST extends Fsm.State, SI extends Fsm.Signal>
	extends SubmissionPublisher<Fsm.Transition<ST, SI>>
{

	private final Stepper<ST, SI> stepper;

	/**
	 * Creates a new signal publisher. The actual transition objects are created
	 * by the given {@code stepper}.
	 *
	 * @param stepper the stepper used by the publisher
	 */
	public SignalPublisher(Stepper<ST, SI> stepper) {
		this.stepper = requireNonNull(stepper);
	}

	/**
	 * Creates a new signal publisher. The actual transition objects are created
	 * by the given {@code stepper}.
	 *
	 * @param stepper the stepper used by the publisher
	 * @param executor the executor to use for async delivery, supporting
	 *        creation of at least one independent thread
	 * @param maxBufferCapacity the maximum capacity for each subscriber's
	 *        buffer (the enforced capacity may be rounded up to the nearest
	 *        power of two and/or bounded by the largest value supported by this
	 *        implementation; method {@link #getMaxBufferCapacity} returns the
	 *        actual value)
	 */
	public SignalPublisher(
		Stepper<ST, SI> stepper,
		Executor executor,
		int maxBufferCapacity
	) {
		this.stepper = requireNonNull(stepper);
		super(requireNonNull(executor), maxBufferCapacity);
	}

	/**
	 * Creates a new signal publisher. The actual transition objects are created
	 * by the given {@code stepper}.
	 *
	 * @param stepper the stepper used by the publisher
	 * @param executor the executor to use for async delivery, supporting
	 *        creation of at least one independent thread
	 * @param maxBufferCapacity the maximum capacity for each subscriber's
	 *        buffer (the enforced capacity may be rounded up to the nearest
	 *        power of two and/or bounded by the largest value supported by this
	 *        implementation; method {@link #getMaxBufferCapacity} returns the
	 *        actual value)
	 * @param handler if non-null, procedure to invoke upon exception thrown in
	 *        method {@code onNext}
	 */
	public SignalPublisher(
		Stepper<ST, SI> stepper,
		Executor executor,
		int maxBufferCapacity,
		BiConsumer<
			? super Flow.Subscriber<? super Fsm.Transition<ST, SI>>,
			? super Throwable> handler
	) {
		this.stepper = requireNonNull(stepper);
		super(requireNonNull(executor), maxBufferCapacity, handler);
	}

	/**
	 * Submits the given {@code signal}, which is transformed by the stepper into
	 * a transition object. The transition is then published to the registered
	 * subscribers.
	 *
	 * @param signal the signal (event) to process
	 * @return the estimated maximum lag among subscribers
	 */
	public synchronized int submit(SI signal) {
		requireNonNull(signal);
		if (isClosed()) {
			throw new IllegalStateException("Closed");
		}

		try {
			final var lag = stepper.next(signal)
				.map(super::submit)
				.orElseGet(this::estimateMaximumLag);

			if (stepper.isFinished()) {
				close();
			}

			return lag;
		} catch (RuntimeException | Error error) {
			if (!isClosed()) {
				closeExceptionally(error);
			}
			throw error;
		}
	}

}
