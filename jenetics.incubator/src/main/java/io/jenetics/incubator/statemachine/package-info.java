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
 * This package contains a simple
 * <a href="https://en.wikipedia.org/wiki/Finite-state_machine#Mathematical_model">
 * finite-state machine</a> implementation. It separates the definition of the
 * FSM from its execution, where the {@link Fsm} implementation is modeled as
 * a quintuple (record) {@code (Σ, S, s0, δ, F)}, with
 * <ul>
 *     <li><b>Σ</b> as the non-empty alphabet of symbols (signals);</li>
 *     <li><b>S</b> as the finites non-empty set of states;</li>
 *     <li><b>s0</b> as the initial state, which is an element of S;</li>
 *     <li><b>δ</b> as the transition function: δ: S x Σ -> S;</li>
 *     <li><b>F</b> as the possible empty set of final states, which are
 *         elements of S.</li>
 * </ul>
 *
 * <h1>Architecture</h1>
 *
 * The static definition of the {@link Fsm} is separated from the actual
 * execution of it. Executing an FSM is essentially the transformation of a
 * stream of {@link Fsm.Signal}s (events) into a stream of state
 * {@link Fsm.Transition}s. Depending on the transition, actual actions can be
 * triggered during the execution.
 * // TODO: add a diagram
 *
 *
 * <h2>Defining an FSM</h2>
 *
 * The {@link Fsm} record implements the FSM quintuple. Since the {@link Fsm} is
 * immutable, it can be shared between threads and can be used for arbitrary
 * executions.
 * <p>
 * The constructor checks the basic FSM invariants: the alphabet and the states
 * must not be empty, the start state must be part of the state set, final states
 * must be part of the state set, the start state must not be final and final
 * states must not have outgoing transitions. States and symbols are identified
 * by their {@link Fsm.Named#name()} and must be unique within one FSM.
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
 *     Fsm.Delta.of(
 *         new Fsm.Transition<>(INACTIVE, BEGIN, ACTIVE),
 *         new Fsm.Transition<>(ACTIVE, PAUSE, PAUSED),
 *         new Fsm.Transition<>(PAUSED, RESUME, ACTIVE),
 *         new Fsm.Transition<>(ACTIVE, END, INACTIVE),
 *         new Fsm.Transition<>(PAUSED, END, INACTIVE),
 *         new Fsm.Transition<>(INACTIVE, EXIT, TERMINATED)
 *     ),
 *      EnumSet.of(TERMINATED)
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
