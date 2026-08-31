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
import java.util.Optional;
import java.util.Set;

import static io.jenetics.incubator.statemachine.FsmTest.Command.BEGIN;
import static io.jenetics.incubator.statemachine.FsmTest.Command.END;
import static io.jenetics.incubator.statemachine.FsmTest.Command.EXIT;
import static io.jenetics.incubator.statemachine.FsmTest.Command.PAUSE;
import static io.jenetics.incubator.statemachine.FsmTest.Command.RESUME;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.ACTIVE;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.INACTIVE;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.PAUSED;
import static io.jenetics.incubator.statemachine.FsmTest.ProcessState.TERMINATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

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

	record NamedState(String name, int index) implements Fsm.State {
	}

	record NamedSymbol(String name, int index) implements Fsm.Symbol {
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
	public void stepperMovesBetweenStates() {
		final var stepper = new SymbolStepper<>(FSM);

		assertThat(stepper.state()).isEqualTo(INACTIVE);
		assertThat(stepper.next(BEGIN))
			.contains(new Fsm.Transition<>(INACTIVE, BEGIN, ACTIVE));
		assertThat(stepper.state()).isEqualTo(ACTIVE);
		assertThat(stepper.next(PAUSE))
			.contains(new Fsm.Transition<>(ACTIVE, PAUSE, PAUSED));
		assertThat(stepper.state()).isEqualTo(PAUSED);
		assertThat(stepper.next(RESUME))
			.contains(new Fsm.Transition<>(PAUSED, RESUME, ACTIVE));
		assertThat(stepper.next(END))
			.contains(new Fsm.Transition<>(ACTIVE, END, INACTIVE));
		assertThat(stepper.next(EXIT))
			.contains(new Fsm.Transition<>(INACTIVE, EXIT, TERMINATED));
		assertThat(stepper.isFinished()).isTrue();
	}

	@Test
	public void stepperIgnoresUndefinedTransition() {
		final var stepper = new SymbolStepper<>(FSM);

		assertThat(stepper.next(PAUSE)).isEmpty();
		assertThat(stepper.state()).isEqualTo(INACTIVE);
	}

	@Test
	public void stepperRejectsTransitionFromFinalState() {
		final var stepper = new SymbolStepper<>(FSM);
		stepper.next(EXIT);

		assertThatIllegalStateException()
			.isThrownBy(() -> stepper.next(END));
	}

	@Test
	public void eventStepperReturnsTransitionWithOriginalEvent() {
		final var stepper = new EventStepper<>(FSM);
		final var event = Fsm.Event.of(BEGIN);

		assertThat(stepper.next(event))
			.contains(new Fsm.Transition<>(INACTIVE, event, ACTIVE));
	}

	@Test
	public void gathererIgnoresUndefinedTransitionsAndStopsAtFinalState() {
		final var events = List.of(BEGIN, PAUSE, RESUME, END, EXIT, END);

		final var transitions = events.stream()
			.gather(Fsm.transitions(() -> new SymbolStepper<>(FSM)))
			.toList();

		assertThat(transitions).containsExactly(
			new Fsm.Transition<>(INACTIVE, BEGIN, ACTIVE),
			new Fsm.Transition<>(ACTIVE, PAUSE, PAUSED),
			new Fsm.Transition<>(PAUSED, RESUME, ACTIVE),
			new Fsm.Transition<>(ACTIVE, END, INACTIVE),
			new Fsm.Transition<>(INACTIVE, EXIT, TERMINATED)
		);
	}

	@Test
	public void deltaRejectsAmbiguousTransitions() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> Fsm.Delta.of(
				new Fsm.Transition<>(INACTIVE, BEGIN, ACTIVE),
				new Fsm.Transition<>(INACTIVE, BEGIN, PAUSED)
			));
	}

	@Test
	public void deltaReturnsEmptyOptionalForMissingTransition() {
		final Fsm.Delta<ProcessState, Command> delta = Fsm.Delta.of(
			new Fsm.Transition<>(INACTIVE, BEGIN, ACTIVE)
		);

		assertThat(delta.apply(INACTIVE, END)).isEqualTo(Optional.empty());
	}

	@Test
	public void fsmRejectsDuplicateStateNames() {
		final var ready1 = new NamedState("ready", 1);
		final var ready2 = new NamedState("ready", 2);
		final var stop = new NamedState("stop", 3);
		final var begin = new NamedSymbol("begin", 1);

		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Fsm<>(
				Set.of(begin),
				Set.of(ready1, ready2, stop),
				ready1,
				Set.of(stop),
				(_, _) -> Optional.empty()
			));
	}

	@Test
	public void fsmRejectsDuplicateSymbolNames() {
		final var ready = new NamedState("ready", 1);
		final var stop = new NamedState("stop", 2);
		final var begin1 = new NamedSymbol("begin", 1);
		final var begin2 = new NamedSymbol("begin", 2);

		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Fsm<>(
				Set.of(begin1, begin2),
				Set.of(ready, stop),
				ready,
				Set.of(stop),
				(_, _) -> Optional.empty()
			));
	}

	@Test
	public void fsmRejectsStartStateOutsideStateSet() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Fsm<>(
				EnumSet.allOf(Command.class),
				EnumSet.of(ACTIVE, TERMINATED),
				INACTIVE,
				EnumSet.of(TERMINATED),
				(_, _) -> Optional.empty()
			));
	}

	@Test
	public void fsmRejectsFinalStateOutsideStateSet() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Fsm<>(
				EnumSet.allOf(Command.class),
				EnumSet.of(INACTIVE, ACTIVE),
				INACTIVE,
				EnumSet.of(TERMINATED),
				(_, _) -> Optional.empty()
			));
	}

	@Test
	public void fsmRejectsStartStateAsFinalState() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Fsm<>(
				EnumSet.allOf(Command.class),
				EnumSet.allOf(ProcessState.class),
				INACTIVE,
				EnumSet.of(INACTIVE),
				(_, _) -> Optional.empty()
			));
	}

	@Test
	public void fsmRejectsTransitionsFromFinalStates() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Fsm<>(
				EnumSet.allOf(Command.class),
				EnumSet.allOf(ProcessState.class),
				INACTIVE,
				EnumSet.of(TERMINATED),
				Fsm.Delta.of(new Fsm.Transition<>(TERMINATED, BEGIN, ACTIVE))
			));
	}

}



