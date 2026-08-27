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
 * Base implementation for stepper interface.
 *
 * @param <ST> the state type
 * @param <SY> the symbol type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.1
 * @since 9.1
 */
abstract class StepperBase<ST extends Fsm.State, SY extends Fsm.Symbol> {

	final Fsm<ST, SY> fsm;
	ST state;

	/**
	 * Create a new stepper object with the given {@code fsm} and
	 * {@code start} state.
	 *
	 * @param fsm   the FSM used by the stepper
	 * @param start the steppers start state
	 * @throws IllegalArgumentException if the given {@code start} state is
	 *         not an element of {@link Fsm#states()}
	 */
	StepperBase(Fsm<ST, SY> fsm, ST start) {
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
	StepperBase(Fsm<ST, SY> fsm) {
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
	 * Sets the current stepper state.
	 *
	 * @param state the new stepper state
	 */
	public synchronized void state(ST state) {
		if (!fsm.states().contains(state)) {
			throw new IllegalArgumentException(
				"Got unknown state: " + state
			);
		}
		this.state = state;
	}

	public synchronized boolean isFinished() {
		return fsm.finals().contains(state);
	}

}
