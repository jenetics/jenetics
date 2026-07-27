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

import static io.jenetics.incubator.statemachine.FsmTest.Command.BEGIN;
import static io.jenetics.incubator.statemachine.FsmTest.Command.END;
import static io.jenetics.incubator.statemachine.FsmTest.Command.EXIT;
import static io.jenetics.incubator.statemachine.FsmTest.Command.PAUSE;
import static io.jenetics.incubator.statemachine.FsmTest.Command.RESUME;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.ACTIVE;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.INACTIVE;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.PAUSED;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.TERMINATED;

import java.util.EnumSet;
import java.util.List;

import org.testng.annotations.Test;

public class FsmTest {

	enum ProcessState implements Fsm.State {
		ACTIVE,
		INACTIVE,
		PAUSED,
		TERMINATED
	}

	enum Command implements Fsm.Symbol, Fsm.Event<Command> {
		BEGIN,
		END,
		PAUSE,
		RESUME,
		EXIT;

		@Override
		public Command kind() {
			return this;
		}
	}

	static final Fsm<ProcessState, Command> FSM = new Fsm<>(
		EnumSet.allOf(Command.class),
		EnumSet.allOf(ProcessState.class),
		INACTIVE,
		EnumSet.of(TERMINATED),
		Fsm.Delta.of(
			new Fsm.Transition<>(INACTIVE, BEGIN, ACTIVE),
			new Fsm.Transition<>(ACTIVE, PAUSE, PAUSED),
			new Fsm.Transition<>(PAUSED, RESUME, ACTIVE),
			new Fsm.Transition<>(ACTIVE, END, INACTIVE),
			new Fsm.Transition<>(PAUSED, END, INACTIVE),
			new Fsm.Transition<>(INACTIVE, EXIT, TERMINATED)
		)
	);

	@Test
	public void stepper() {
		final var stepper = new Fsm.Stepper<>(FSM);
		final var events = List.of(BEGIN, PAUSE, RESUME, END, EXIT, END);

		for (var it = events.iterator(); it.hasNext() && !stepper.isFinished();) {
			System.out.println(stepper.next(it.next()));
		}
	}

	@Test
	public void submitting() {
		var publisher = new Fsm.EventPublisher<>(
			FSM, FSM.start(),
			(event, before, after) -> IO.println(
				"%s: %s -> %s".formatted(event, before, after)
			)
		);

		final var events = List.of(BEGIN, PAUSE, RESUME, END, EXIT, END);
		for (var it = events.iterator(); it.hasNext() && !publisher.isFinished();) {
			publisher.submit(it.next());
		}

	}

	@Test
	public void terminating() {
		final var publisher = new Fsm.EventPublisher<>(FSM, FSM.start(), (_, _, _) -> {});

		final var events = List.of(BEGIN, PAUSE, RESUME, END, EXIT, END);
		events.stream()
			.takeWhile(_ -> !publisher.isFinished())
			.peek(publisher::submit)
			.forEach(System.out::println);
	}

	@Test
	public void transitionGatherer() {
		final var events = List.of(BEGIN, PAUSE, RESUME, END, EXIT, END);

		events.stream()
			.gather(Fsm.transitions(FSM))
			.forEach(System.out::println);

	}

}




