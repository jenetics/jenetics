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

import static java.util.Objects.requireNonNull;

public class EventStepper<
	ST extends Fsm.State,
	SY extends Fsm.Symbol,
	E extends Fsm.Event<SY>
>
	extends StepperBase<ST, SY>
	implements Fsm.Stepper<ST, E>
{

	/**
	 * Create a new stepper object with the given {@code fsm} and
	 * {@code start} state.
	 *
	 * @param fsm   the FSM used by the stepper
	 * @param start the steppers start state
	 * @throws IllegalArgumentException if the given {@code start} state is
	 *         not an element of {@link Fsm#states()}
	 */
	public EventStepper(Fsm<ST, SY> fsm, ST start) {
		super(fsm, start);
	}

	/**
	 * Create a new stepper object with the given {@code fsm} and the
	 * {@link Fsm#start()} state.
	 *
	 * @param fsm the FSM used by the stepper
	 */
	public EventStepper(Fsm<ST, SY> fsm) {
		super(fsm, fsm.start());
	}

	@Override
	public synchronized Optional<Fsm.Transition<ST, E>> next(E event) {
		requireNonNull(event);
		if (!fsm.alphabet().contains(event.kind())) {
			throw new IllegalArgumentException(
				"Got unknown signal: " + event.kind()
			);
		}
		if (isFinished()) {
			throw new IllegalStateException(
				"No transition triggered by '%s', already in a final state '%s'."
					.formatted(event, state)
			);
		}

		final var next = fsm.delta().apply(state, event.kind()).orElse(null);

		final Fsm.Transition<ST, E> transition;
		if (next != null) {
			transition = Fsm.Transition.of(state, event, next);
			state = next;
			return Optional.of(transition);
		} else {
			return Optional.empty();
		}
	}

}
