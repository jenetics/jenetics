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

import org.testng.annotations.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Gatherer;

import static io.jenetics.incubator.statemachine.FsmTest.Command.BEGIN;
import static io.jenetics.incubator.statemachine.FsmTest.Command.END;
import static io.jenetics.incubator.statemachine.FsmTest.Command.EXIT;
import static io.jenetics.incubator.statemachine.FsmTest.Command.PAUSE;
import static io.jenetics.incubator.statemachine.FsmTest.Command.RESUME;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.ACTIVE;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.INACTIVE;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.PAUSED;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.TERMINATED;

public class FsmTest {

	enum ProcessState implements Fsm.State {
		ACTIVE,
		INACTIVE,
		PAUSED,
		TERMINATED
	}

	enum Command implements Fsm.Symbol, Fsm.Event {
		BEGIN,
		END,
		PAUSE,
		RESUME,
		EXIT;

		@Override
		public Fsm.Symbol kind() {
			return this;
		}
	}

	static final Fsm FSM = new Fsm(
		EnumSet.allOf(Command.class),
		EnumSet.allOf(ProcessState.class),
		INACTIVE,
		EnumSet.of(TERMINATED),
		Set.of(
			new Fsm.Transition(INACTIVE, BEGIN, ACTIVE),
			new Fsm.Transition(ACTIVE, PAUSE, PAUSED),
			new Fsm.Transition(PAUSED, RESUME, ACTIVE),
			new Fsm.Transition(ACTIVE, END, INACTIVE),
			new Fsm.Transition(PAUSED, END, INACTIVE),
			new Fsm.Transition(INACTIVE, EXIT, TERMINATED)
		)
	);

	@Test
	public void submitting() {
		final var publisher = new Fsm.EventPublisher(
			FSM,
			INACTIVE,
			(_, event, prev, next) -> IO.println(
				"%s: %s -> %s".formatted(event, prev, next)
			)
		);

		publisher.submit(BEGIN);
		publisher.submit(PAUSE);
		publisher.submit(RESUME);
		publisher.submit(END);
		publisher.submit(EXIT);
	}

	@Test
	public void eventStream() {
		final var events = List.of(BEGIN, PAUSE, RESUME, END, EXIT, END);

		events.stream()
			.gather(states(FSM))
			.forEach(System.out::println);

	}

	record EventState(Fsm.Event event, Fsm.State prev, Fsm.State next) {
	}

	static Gatherer<Fsm.Event, ?, EventState> states(Fsm fsm, Fsm.State start) {
		final class State {
			Fsm.State current = start;
		}

		return Gatherer.ofSequential(
			State::new,
			Gatherer.Integrator.of((state, event, downstream) -> {
				var next = fsm.delta().apply(state.current, event.kind());
				next.ifPresent(n -> {
					downstream.push(new EventState(event, state.current, n));
					state.current = n;
				});
				return !fsm.finals().contains(next.orElse(state.current));
			})
		);
	}

	static Gatherer<Fsm.Event, ?, EventState> states(Fsm fsm) {
		return states(fsm, fsm.start());
	}

}




