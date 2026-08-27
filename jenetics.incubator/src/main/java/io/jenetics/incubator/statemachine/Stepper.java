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

/**
 * This class steps through the FSM, with the {@link #next(Fsm.Symbol)} method.
 * and holds the current state.
 *
 * @implNote
 * The stepper state is protected by a mutex and can therefore be shared between
 * different threads.
 *
 * @param <ST> the state type
 * @param <SY> the symbol (signal) type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.1
 * @since 9.1
 */
public final class Stepper<ST extends Fsm.State, SY extends Fsm.Symbol> {
	private final Fsm<ST, SY> fsm;
	private ST state;

	/**
	 * Create a new stepper object with the given {@code fsm} and
	 * {@code start} state.
	 *
	 * @param fsm   the FSM used by the stepper
	 * @param start the steppers start state
	 * @throws IllegalArgumentException if the given {@code start} state is
	 *                                  not an element of {@link Fsm#states()}
	 */
	public Stepper(Fsm<ST, SY> fsm, ST start) {
		this.fsm = requireNonNull(fsm);
		this.state = requireNonNull(start);

		if (!fsm.states().contains(state)) {
			throw new IllegalArgumentException(
				"Initial state '%s' is not part of available states, %s."
					.formatted(state, fsm.states())
			);
		}
	}

	/**
	 * Create a new stepper object with the given {@code fsm} and the
	 * {@link Fsm#start()} state.
	 *
	 * @param fsm the FSM used by the stepper
	 */
	public Stepper(Fsm<ST, SY> fsm) {
		this(fsm, fsm.start());
	}

	/**
	 * Returns the current state.
	 *
	 * @return the current state
	 */
	public synchronized ST state() {
		return state;
	}

	/**
	 * Return {@code true} if the current state is an element of the
	 * {@link Fsm#finals()} states.
	 *
	 * @return {@code true} if the current state is an element of the
	 * {@link Fsm#finals()} states, {@code false} otherwise.
	 */
	public synchronized boolean isFinished() {
		return fsm.finals().contains(state);
	}

	/**
	 * Moves the current state to the next state by applying the given
	 * {@code signal}.
	 *
	 * @param signal the signal which moves the current step to the next step
	 * @return the step result. If the step is {@link Step.Invalid}, the current
	 * step is not changed.
	 * @throws IllegalStateException if the {@link #isFinished()} is {@code true}
	 * @throws IllegalArgumentException if the given {@code signal} is not
	 *         an element of {@link Fsm#alphabet()}
	 */
	public synchronized Step<ST, SY> next(SY signal) {
		requireNonNull(signal);
		if (!fsm.alphabet().contains(signal)) {
			throw new IllegalArgumentException(
				"Got unknown signal: " + signal
			);
		}
		if (isFinished()) {
			throw new IllegalStateException(
				"No transition triggered by '%s', already in a final state '%s'."
					.formatted(signal, state)
			);
		}

		final var next = fsm.delta().apply(state, signal).orElse(null);

		final Step<ST, SY> step;
		if (next != null) {
			step = new Step.Valid<>(state, signal, next);
			state = next;
		} else {
			step = new Step.Invalid<>(state, signal);
		}

		return step;
	}

}
