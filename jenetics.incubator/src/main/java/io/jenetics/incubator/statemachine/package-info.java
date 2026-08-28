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

/**
 * This package contains a small
 * <a href="https://en.wikipedia.org/wiki/Finite-state_machine#Mathematical_model">
 * finite-state machine</a> implementation. It models the state machine itself
 * as an immutable definition and keeps execution state in separate, mutable
 * steppers.
 * <p>
 * The following diagram shows the interaction between the main components:
 *
 * <pre>{@code
 * +-------------+       uses definition        +----------------+
 * | Fsm         |<-----------------------------| Stepper        |
 * |-------------|                              |----------------|
 * | alphabet    |                              | state()        |
 * | states      |                              | isFinished()   |
 * | start       |                              | next(signal)   |
 * | finals      |                              +-------^--------+
 * | delta       |                                      |
 * +-------------+                                      |
 *                                                      | delegates to
 *                                                      |
 *                                              +-------+--------+
 *                                              | SignalPublisher|
 *                                              |----------------|
 *                          Application ----->  | submit(signal) |
 *                          submits Fsm.Signal  | subscribe(...) |
 *                                              | close()        |
 *                                              +-------+--------+
 *                                                      |
 *                                                      | emits Fsm.Transition
 *                                                      v
 *                                              Flow.Subscriber
 * }</pre>
 *
 * <h2>Definition</h2>
 *
 * The {@link Fsm} record is the immutable FSM definition. It contains the
 * input alphabet, the available states, the start state, the final states and
 * the partial transition function, {@link Fsm.Delta}. A state machine is
 * usually defined once and stored as a static constant.
 * <p>
 * The constructor checks the basic FSM invariants: the alphabet and the states
 * must not be empty, the start state must be part of the state set, final states
 * must be part of the state set, the start state must not be final and final
 * states must not have outgoing transitions. States and symbols are identified
 * by their {@link Fsm.Named#name()} and must be unique within one FSM.
 *
 * {@snippet lang=java:
 * enum ProcessState implements Fsm.State {
 *     ACTIVE,
 *     INACTIVE,
 *     PAUSED,
 *     TERMINATED
 * }
 *
 * enum Command implements Fsm.Symbol, Fsm.Event<Command> {
 *     BEGIN,
 *     END,
 *     PAUSE,
 *     RESUME,
 *     EXIT;
 *
 *     @Override
 *     public Command kind() {
 *         return this;
 *     }
 * }
 *
 * static final Fsm<ProcessState, Command> FSM = new Fsm<>(
 *     EnumSet.allOf(Command.class),
 *     EnumSet.allOf(ProcessState.class),
 *     INACTIVE,
 *     EnumSet.of(TERMINATED),
 *     Fsm.Delta.of(
 *         new Fsm.Transition<>(INACTIVE, BEGIN, ACTIVE),
 *         new Fsm.Transition<>(ACTIVE, PAUSE, PAUSED),
 *         new Fsm.Transition<>(PAUSED, RESUME, ACTIVE),
 *         new Fsm.Transition<>(ACTIVE, END, INACTIVE),
 *         new Fsm.Transition<>(PAUSED, END, INACTIVE),
 *         new Fsm.Transition<>(INACTIVE, EXIT, TERMINATED)
 *     )
 * );
 * }
 *
 * <h2>Execution</h2>
 *
 * A {@link Stepper} executes the state machine and holds the current state.
 * Calling {@link Stepper#next(Fsm.Signal)} applies one signal and returns the
 * performed {@link Fsm.Transition}, if a transition is defined. Undefined
 * transitions are ignored by returning an empty {@link java.util.Optional};
 * unknown signals and signals after reaching a final state are rejected.
 * <p>
 * {@link SymbolStepper} accepts plain {@link Fsm.Symbol symbols}. Use it when
 * the signal alone is enough to decide the transition.
 *
 * {@snippet lang=java:
 * final var stepper = new SymbolStepper<>(FSM);
 *
 * stepper.next(BEGIN);  // INACTIVE -> ACTIVE
 * stepper.next(PAUSE);  // ACTIVE -> PAUSED
 * stepper.next(RESUME); // PAUSED -> ACTIVE
 * stepper.next(END);    // ACTIVE -> INACTIVE
 * stepper.next(EXIT);   // INACTIVE -> TERMINATED
 *
 * assert stepper.isFinished();
 * }
 *
 * {@link EventStepper} accepts {@link Fsm.Event events}. Events expose a
 * {@link Fsm.Event#kind()} symbol and may carry additional payload, for example
 * by using {@link PayloadEvent}.
 *
 * {@snippet lang=java:
 * final var stepper = new EventStepper<>(FSM);
 * final var event = Fsm.Event.of(BEGIN);
 *
 * stepper.next(event)
 *     .ifPresent(transition -> handle(transition));
 * }
 *
 * <h2>Streams and Reactive Use</h2>
 *
 * For stream processing, {@link Fsm#transitions(java.util.function.Supplier)}
 * returns a gatherer that enriches a signal stream with the matching
 * transitions. The gatherer ignores signals without a defined transition and
 * stops once the stepper reaches a final state.
 *
 * {@snippet lang=java:
 * events.stream()
 *     .gather(Fsm.transitions(() -> new SymbolStepper<>(FSM)))
 *     .forEach(transition -> handle(transition));
 * }
 *
 * The same execution model can be used with the {@link java.util.concurrent.Flow}
 * API. {@link SignalPublisher} accepts input signals, publishes resulting
 * transitions and closes when the stepper reaches a final state.
 * {@link SignalSubscriber} is a small subscriber adapter which requests one
 * transition at a time and forwards transitions to a {@link java.util.function.Consumer}.
 *
 * {@snippet lang=java:
 * try (var publisher = new SignalPublisher<>(new SymbolStepper<>(FSM))) {
 *     publisher.subscribe(new SignalSubscriber<>(transition -> handle(transition)));
 *
 *     publisher.submit(BEGIN);
 *     publisher.submit(END);
 *     publisher.submit(EXIT);
 * }
 * }
 *
 * <h2>Observing Transitions</h2>
 *
 * {@link OberservableStepper} wraps an existing stepper and notifies registered
 * listeners whenever the wrapped stepper performs a transition. It is useful
 * when state transitions should trigger actions without mixing those actions
 * into the FSM definition.
 *
 * {@snippet lang=java:
 * final var stepper = new OberservableStepper<>(new SymbolStepper<>(FSM));
 *
 * stepper.register(transition -> audit(transition));
 * stepper.next(BEGIN);
 * }
 *
 * @apiNote
 * This package does not prescribe how user actions are executed for a
 * transition. It creates and transports {@link Fsm.Transition} values; calling
 * application code for those transitions remains the responsibility of the
 * caller.
 * <p>
 * The {@link Fsm} definition is immutable and can be shared between threads.
 * Stepper implementations keep mutable execution state and synchronize state
 * access.
 *
 * @see Fsm
 * @see Stepper
 * @see SymbolStepper
 * @see EventStepper
 * @see SignalPublisher
 * @see SignalSubscriber
 * @see OberservableStepper
 */
package io.jenetics.incubator.statemachine;
