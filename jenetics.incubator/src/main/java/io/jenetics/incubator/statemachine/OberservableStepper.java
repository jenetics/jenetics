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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public final class OberservableStepper<ST extends Fsm.State, SI extends Fsm.Signal>
	implements Fsm.Stepper<ST, SI>
{

	private final Fsm.Stepper<ST, SI> adoptee;

	private final List<Consumer<? super Fsm.Transition<ST, SI>>>
		listeners = new CopyOnWriteArrayList<>();

	public OberservableStepper(final Fsm.Stepper<ST, SI> adoptee) {
		this.adoptee = requireNonNull(adoptee);
	}

	@Override
	public ST state() {
		return adoptee.state();
	}

	@Override
	public boolean isFinished() {
		return adoptee.isFinished();
	}

	@Override
	public Optional<Fsm.Transition<ST, SI>> next(SI signal) {
		final var result = adoptee.next(signal);
		result.ifPresent(t -> listeners.forEach(c -> c.accept(t)));
		return result;
	}

	public void register(final Consumer<? super Fsm.Transition<ST, SI>> listener) {
		listeners.add(requireNonNull(listener));
	}

}
