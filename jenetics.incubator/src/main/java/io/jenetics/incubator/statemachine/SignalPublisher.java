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

import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.BiConsumer;

import static java.util.Objects.requireNonNull;

/**
 * Reactive signal transition publisher.
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

	private final Fsm.Stepper<ST, SI> stepper;

	public SignalPublisher(Fsm.Stepper<ST, SI> stepper) {
		this.stepper = requireNonNull(stepper);
	}

	public SignalPublisher(
		Fsm.Stepper<ST, SI> stepper,
		Executor executor,
		int maxBufferCapacity
	) {
		super(requireNonNull(executor), maxBufferCapacity);
		this.stepper = requireNonNull(stepper);
	}

	public SignalPublisher(
		Fsm.Stepper<ST, SI> stepper,
		Executor executor,
		int maxBufferCapacity,
		BiConsumer<? super Flow.Subscriber<? super Fsm.Transition<ST, SI>>, ? super Throwable> handler
	) {
		super(requireNonNull(executor), maxBufferCapacity, handler);
		this.stepper = requireNonNull(stepper);
	}

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
