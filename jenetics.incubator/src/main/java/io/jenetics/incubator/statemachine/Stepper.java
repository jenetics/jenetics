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

import java.util.Optional;

/**
 * A stepper is responsible for performing the state transitions for given
 * signals. Implementations will be mutable and update the current state
 * according the transition function, <em>delta</em>, defined by the FSM.
 *
 * @param <ST> the state type
 * @param <SI> the symbol (signal) type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.1
 * @since 9.1
 */
public interface Stepper<ST extends Fsm.State, SI extends Fsm.Signal> {

	/**
	 * Returns the current stepper state.
	 *
	 * @return the current stepper state
	 */
	ST state();

	/**
	 * Return {@code true} if the current state is an element of the
	 * {@link Fsm#finals()} states.
	 *
	 * @return {@code true} if the current state is an element of the
	 * {@link Fsm#finals()} states, {@code false} otherwise.
	 */
	boolean isFinished();

	/**
	 * Moves the current state to the next state by applying the given
	 * {@code signal}.
	 *
	 * @param signal the signal which moves the current step to the next step
	 * @return the transition, if any
	 * @throws IllegalStateException if the {@link #isFinished()} is {@code true}
	 * @throws IllegalArgumentException if the given {@code signal} is not
	 *         an element of {@link Fsm#alphabet()}
	 */
	Optional<Fsm.Transition<ST, SI>> next(SI signal);

}
