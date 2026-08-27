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
 * The base interface for an FSM step. A step might be {@link Valid}, if the
 * {@link Fsm.Delta} function is defined for a given {@link Fsm.Symbol}, or
 * {@link Invalid}, if such a transition is not defined.
 *
 * @param <ST> the state type
 * @param <SY> the symbol (signal) type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.1
 * @since 9.1
 */
public sealed interface Step<ST extends Fsm.State, SY extends Fsm.Symbol> {

	/**
	 * Return the state before the step (transition).
	 *
	 * @return the state before the step (transition)
	 */
	ST before();

	/**
	 * Return the signal which triggered the step (transition).
	 *
	 * @return the signal which triggered the step (transition)
	 */
	SY signal();

	/**
	 * A valid step result.
	 *
	 * @param before the state before the step
	 * @param signal the transition symbol
	 * @param after  the state after the step
	 * @param <ST>   the state type
	 * @param <SY>   the symbol (signal) type
	 */
	record Valid<ST extends Fsm.State, SY extends Fsm.Symbol>(
		ST before,
		SY signal,
		ST after
	)
		implements Step<ST, SY>, Fsm.Transition<ST, SY>
	{
		public Valid {
			requireNonNull(before);
			requireNonNull(signal);
			requireNonNull(after);
		}
	}

	/**
	 * An invalid step result, which is not defined by the {@link Fsm.Delta}
	 * function of the {@link Fsm}.
	 *
	 * @param before the state before the step
	 * @param signal the transition symbol
	 * @param <ST>   the state type
	 * @param <SY>   the symbol (signal) type
	 */
	record Invalid<ST extends Fsm.State, SY extends Fsm.Symbol>(
		ST before,
		SY signal
	)
		implements Step<ST, SY> {
		public Invalid {
			requireNonNull(before);
			requireNonNull(signal);
		}
	}
}
