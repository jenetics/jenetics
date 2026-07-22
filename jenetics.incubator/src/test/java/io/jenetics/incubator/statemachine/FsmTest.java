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
import java.util.Set;

import org.testng.annotations.Test;

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
	public void running() {
		final var publisher = new Fsm.EventPublisher(
			FSM,
			INACTIVE,
			(_, event, prev, next) -> IO.println(
				"%s: %s -> %s".formatted(event, prev, next)
			)
		);

		publisher.publish(BEGIN);
		publisher.publish(PAUSE);
		publisher.publish(RESUME);
		publisher.publish(END);
		publisher.publish(EXIT);
	}

}
