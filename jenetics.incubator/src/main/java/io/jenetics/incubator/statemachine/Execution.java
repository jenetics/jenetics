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

/**
 * Defines the actual transition execution.
 *
 * @param <ST> the state type
 * @param <SI> the symbol type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.1
 * @since 9.1
 */
@FunctionalInterface
public interface Execution<ST extends Fsm.State, SI extends Fsm.Signal> {

	/**
	 * Returns the action to be executed for the given state {@code transition}.
	 *
	 * @param transition the state transition
	 * @return the action to be executed for the given state {@code transition}
	 */
	Runnable apply(Fsm.Transition<ST, SI> transition);

}
