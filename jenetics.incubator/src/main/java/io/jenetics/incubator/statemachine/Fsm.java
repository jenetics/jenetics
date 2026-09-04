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
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;

/**
 * Implements a
 * <a href="https://en.wikipedia.org/wiki/Finite-state_machine#Mathematical_model">
 * Finit State Machine</a> as a quintuple (record) (Σ, S, s<sub>0</sub>, δ, F),
 * where:
 * <ul>
 *     <li><b>Σ</b> is the non-empty alphabet of symbols (signals);</li>
 *     <li><b>S</b> is the finite non-empty set of states;</li>
 *     <li><b>s<sub>0</sub></b> is the initial state, which is an element of S;</li>
 *     <li><b>δ</b> is the transition function: δ: S &#10005; Σ &rarr; S;</li>
 *     <li><b>F</b> is the possible empty set of final states, which are
 *         elements of S.</li>
 * </ul>
 *
 * A {@link Fsm} instance is usually created as static constant.
 *
 * <h1>State machine</h1>
 *
 * Since the FSM is immutable, it can be defined as static constant and shared
 * between different threads and executions.
 * {@snippet lang=java:
 * enum ProcessState implements Fsm.State {
 *     ACTIVE, INACTIVE, PAUSED, TERMINATED
 * }
 * enum Command implements Fsm.Symbol, Fsm.Event<Command> {
 *     BEGIN, END, PAUSE, RESUME, EXIT;
 *     @Override public Command kind() {
 *         return this;
 *     }
 * }
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
 *     EnumSet.of(TERMINATED)
 * );
 * }
 *
 * <h1>State transition</h1>
 *
 * The {@link Fsm} class itself doesn't hold any mutable state. This is the
 * responsibility of the {@link Stepper} class, which maintains a mutable state
 * and can perform state transitions on incoming {@link Fsm.Signal}s using an
 * FSM definition
 * {@snippet lang=java:
 * final var events = List.of(BEGIN, PAUSE, RESUME, END, EXIT, END);
 * events.stream()
 *     // Converts the stream of events into a stream of state transitions.
 *     .gather(Fsm.transitions(() -> new EventStepper<>(FSM)))
 *     .forEach(IO::println);
 *
 * // Transition[before=INACTIVE, signal=BEGIN, after=ACTIVE]
 * // Transition[before=ACTIVE, signal=PAUSE, after=PAUSED]
 * // Transition[before=PAUSED, signal=RESUME, after=ACTIVE]
 * // Transition[before=ACTIVE, signal=END, after=INACTIVE]
 * // Transition[before=INACTIVE, signal=EXIT, after=TERMINATED]
 * }
 *
 * You can also use the stepper directly for an imperative state transitions.
 * {@snippet lang=java:
 * final var stepper = new EventStepper<>(FSM);
 * for (int i = 0; i < events.size() && !stepper.isFinished(); ++i) {
 *     final var transition = stepper.next(events.get(i));
 *     IO.println(transition);
 * }
 *
 * // Optional[Transition[before=INACTIVE, signal=BEGIN, after=ACTIVE]]
 * // Optional[Transition[before=ACTIVE, signal=PAUSE, after=PAUSED]]
 * // Optional[Transition[before=PAUSED, signal=RESUME, after=ACTIVE]]
 * // Optional[Transition[before=ACTIVE, signal=END, after=INACTIVE]]
 * // Optional[Transition[before=INACTIVE, signal=EXIT, after=TERMINATED]]
 * }
 *
 * @apiNote
 * This API has no concept about the execution of action on state transitions.
 * It only allows you to create a <em>stream</em> of {@link Transition} objects
 * out of a <em>stream</em> of {@link Signal}s. Executing user actions
 * on state transitions must be implemented by the user. In the simplest case,
 * a static methods is called for every transition.
 *
 * @implNote
 * The {@link Fsm} class is immutable and thread safe and can be shared between
 * different threads and processors (signal publisher).
 *
 * @see Stepper
 * @see SymbolStepper
 * @see SignalPublisher
 * @see SignalSubscriber
 *
 * @param alphabet the input alphabet (a finite non-empty set of symbols)
 * @param states the finite non-empty set of states
 * @param start the initial state, an element of {@link #states()}
 * @param finals the set of final states, a (possibly empty) subset of
 *        {@link #states()}
 * @param delta the state-transition function
 * @param <ST> the state type
 * @param <SY> the symbol (signal) type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.1
 * @since 9.1
 */
public record Fsm<ST extends Fsm.State, SY extends Fsm.Symbol>(
	Set<SY> alphabet,
	Set<ST> states,
	ST start,
	Delta<ST, SY> delta,
	Set<ST> finals
) {

	public Fsm {
		alphabet = Set.copyOf(alphabet);
		states = Set.copyOf(states);
		requireNonNull(start);
		finals = Set.copyOf(finals);
		requireNonNull(delta);

		if (alphabet.isEmpty()) {
			throw new IllegalArgumentException("The symbols must not be empty.");
		}

		if (states.isEmpty()) {
			throw new IllegalArgumentException("The states must not be empty.");
		}

		final var duplicateSymbols = Named.duplicates(alphabet);
		if (!duplicateSymbols.isEmpty()) {
			throw new IllegalArgumentException(
				"Alphabet contains duplicate symbols: " + duplicateSymbols
			);
		}

		final var duplicateStates = Named.duplicates(states);
		if (!duplicateStates.isEmpty()) {
			throw new IllegalArgumentException(
				"States contains duplicate entries: " + duplicateStates
			);
		}

		if (!states.contains(start)) {
			throw new IllegalArgumentException(
				"Start state '%s' is not part of available states, %s."
					.formatted(start, states)
			);
		}

		if (finals.contains(start)) {
			throw new IllegalArgumentException(
				"Start state '%s' must not be a final state, %s."
					.formatted(start, finals)
			);
		}

		final var abth = alphabet;
		final var finalsWithTransition = finals.stream()
			.filter(f -> abth.stream().anyMatch(a -> delta.apply(f, a).isPresent()))
			.toList();
		if (!finalsWithTransition.isEmpty()) {
			throw new IllegalArgumentException(
				"No transition allowed from final state: " + finalsWithTransition
			);
		}

		final var missing = finals.stream()
			.filter(not(states::contains))
			.toList();
		if (!missing.isEmpty()) {
			throw new IllegalArgumentException(
				"Final states %s are not part of the available states, %s."
					.formatted(missing, states)
			);
		}

		// Final states must not have further transitions.
		final var finished = finals;
		final List<Transition<ST, SY>> transitions = alphabet.stream()
			.flatMap(sy -> finished.stream().map(st -> Map.entry(st, sy)))
			.flatMap(step -> delta.apply(step.getKey(), step.getValue())
				.map(to -> new Transition<>(step.getKey(), step.getValue(), to))
				.stream()
			)
			.toList();
		if (!transitions.isEmpty()) {
			throw new IllegalArgumentException(
				"Found transitions from final state(s): %s.".formatted(
					transitions.stream()
						.map(Objects::toString)
						.collect(Collectors.joining(", "))
				)
			);
		}

	}

	/* *************************************************************************
	 * Classes and interfaces for defining the FSM.
	 * ************************************************************************/

	/**
	 * Base interface for {@link Symbol} and {@link Event} interfaces. The purpose
	 * of a signal is to initiate a state transition, depending on the scope.
	 */
	public sealed interface Signal {
	}

	public sealed interface Named {

		/**
		 * Return the object name.
		 *
		 * @return the object name
		 */
		String name();

		private static <N extends Named> List<N>
		duplicates(Collection<? extends N> values) {
			final var grouped = values.stream()
				.collect(Collectors.groupingBy(Named::name));

			return grouped.values().stream()
				.filter(ns -> ns.size() > 1)
				.map(List::getFirst)
				.sorted(Comparator.comparing(Named::name))
				.collect(Collectors.toUnmodifiableList());
		}

	}

	/**
	 * Interface for FSM symbols. A set of symbols form the alphabet of the FSM.
	 */
	public non-sealed interface Symbol extends Signal, Named {

		/**
		 * Return the symbol name.
		 *
		 * @return the symbol name
		 */
		@Override
		String name();

		/**
		 * Return a new symbol with the given {@code name}.
		 *
		 * @param name the symbol name
		 * @return a new symbol with the given name
		 */
		static Symbol of(String name) {
			record SimpleSymbol(String name) implements Symbol {
				SimpleSymbol {
					requireNonNull(name);
				}
				@Override
				public String toString() {
					return name;
				}
			}

			return new SimpleSymbol(name);
		}

	}

	/**
	 * Interface for FSM transition events. Events may hold additional payload.
	 *
	 * @param <SY> the symbol (signal) type
	 */
	public non-sealed interface Event<SY extends Symbol> extends Signal {

		/**
		 * Return the symbol, this signal belongs to.
		 *
		 * @return the signal symbol
		 */
		SY kind();

		/**
		 * Return a new event of the given {@code kind}.
		 *
		 * @param kind the event kind
		 * @return a new event of the given {@code kind}
		 * @param <SY> the event kind type
		 */
		static <SY extends Symbol> Event<SY> of(SY kind) {
			record SimpleEvent<SY extends Symbol>(SY kind) implements Event<SY> {
				SimpleEvent {
					requireNonNull(kind);
				}
			}

			return new  SimpleEvent<>(kind);
		}
	}

	/**
	 * Interface for the FSM states.
	 */
	public non-sealed interface State extends Named {

		/**
		 * Return the state name. The name must be unique within a {@link Fsm}.
		 *
		 * @return the name of the state
		 */
		@Override
		String name();

		/**
		 * Return a new state with the given {@code name}.
		 *
		 * @param name the state name
		 * @return a new state with the given name
		 */
		static State of(String name) {
			record SimpleState(String name) implements State {
				SimpleState {
					requireNonNull(name);
				}
				@Override
				public String toString() {
					return name;
				}
			}

			return new SimpleState(name);
		}
	}

	/**
	 * The <em>partial</em> state transition function.
	 *
	 * @param <ST> the state type
	 * @param <SY> the symbol (signal) type
	 */
	@FunctionalInterface
	public interface Delta<ST extends State, SY extends Symbol> {

		/**
		 * Applies the transition from the {@code current} state to the next
		 * state when the {@code symbol} is signaled.
		 *
		 * @param current the current state
		 * @param symbol the signaled symbol
		 * @return the next state, if a transition is defined
		 */
		Optional<ST> apply(ST current, SY symbol);

		/**
		 * Applies the transition from the {@code current} state to the next
		 * state when the {@code event} is signaled. The default implementation
		 * forwards this call to {@link #apply(State, Symbol)}, by using the
		 * event kind, {@link Event#kind()}.
		 *
		 * @apiNote
		 * Users might want to override this method, when a greater flexibility
		 * is required for the state transition, e.g. when one want to implement
		 * <em>guards</em>.
		 *
		 * @see #of(BiFunction)
		 * @see #of(Function)
		 *
		 * @param current the current state
		 * @param event the signaled event
		 * @return the next state, if a transition is defined
		 */
		default Optional<ST> apply(ST current, Event<SY> event) {
			return apply(current, event.kind());
		}

		/**
		 * Creates a <em>delta</em> function from the given set of
		 * {@code transitions}.
		 *
		 * @see #of(Transition[])
		 * @see #of(BiFunction)
		 *
		 * @param transitions the state transitions which defines the
		 *        <em>delta</em> function
		 * @param <ST> the state type
		 * @param <SY> the symbol (signal) type
		 * @return a <em>delta</em> function from the given set of
		 *         {@code transitions}
		 * @throws IllegalArgumentException if the transitions have multiple
		 *         (before, end) pairs.
		 */
		static <ST extends State, SY extends Symbol>
		Delta<ST, SY> of(Set<Transition<ST, SY>> transitions) {
			final Map<StateSignal<ST, SY>, List<Transition<ST, SY>>> map =
				transitions.stream()
					.collect(groupingBy(t -> new StateSignal<>(t.before(), t.signal())));

			final List<StateSignal<ST, SY>> duplicates = map.entrySet().stream()
				.filter(t -> t.getValue().size() > 1)
				.map(Map.Entry::getKey)
				.toList();

			if (!duplicates.isEmpty()) {
				throw new IllegalArgumentException(
					"Found ambiguous transitions: %s.".formatted(
						duplicates.stream()
							.map(ss -> "(%s, %s)".formatted(ss.state, ss.signal))
							.collect(Collectors.joining(", "))
					)
				);
			}

			return (state, symbol) -> Optional
				.ofNullable(map.get(new StateSignal<>(state, symbol)))
				.map(t -> t.getFirst().after());
		}

		/**
		 * Creates a <em>delta</em> function from the given set of
		 * {@code transitions}.
		 * {@snippet lang=java:
		 * Fsm.Delta.of(
		 *     new Fsm.Transition<>(INACTIVE, BEGIN, ACTIVE),
		 *     new Fsm.Transition<>(ACTIVE, PAUSE, PAUSED),
		 *     new Fsm.Transition<>(PAUSED, RESUME, ACTIVE),
		 *     new Fsm.Transition<>(ACTIVE, END, INACTIVE),
		 *     new Fsm.Transition<>(PAUSED, END, INACTIVE),
		 *     new Fsm.Transition<>(INACTIVE, EXIT, TERMINATED)
		 * )
		 * }
		 *
		 * @see #of(Set)
		 * @see #of(BiFunction)
		 *
		 * @param transitions the state transitions which defines the
		 *        <em>delta</em> function
		 * @param <ST> the state type
		 * @param <SY> the symbol (signal) type
		 * @return a <em>delta</em> function from the given set of
		 *         {@code transitions}
		 * @throws IllegalArgumentException if the transitions have multiple
		 *         (before, end) pairs.
		 */
		@SafeVarargs
		static <ST extends State, SY extends Symbol>
		Delta<ST, SY> of(Transition<ST, SY>... transitions) {
			return of(Set.of(transitions));
		}

		/**
		 * Creates a <em>delta</em> function from the given (transition)
		 * bi-function {@code fn}.
		 * {@snippet lang=java:
		 * Fsm.Delta.of((source, signal) -> {
		 *     final var target = switch (source) {
		 *         case INACTIVE -> switch (signal) {
		 *             case BEGIN -> ACTIVE;
		 *             case EXIT -> TERMINATED;
		 *             default -> null;
		 *         };
		 *         case ACTIVE -> switch (signal) {
		 *             case PAUSE -> PAUSED;
		 *             case END -> INACTIVE;
		 *             default -> null;
		 *         };
		 *         case PAUSED -> switch (signal) {
		 *             case RESUME -> ACTIVE;
		 *             case END -> INACTIVE;
		 *             default -> null;
		 *         };
		 *         default -> null;
		 *     };
		 *     return Optional.ofNullable(target);
		 * })
		 * }
		 *
		 * @see #of(Transition[])
		 * @see #of(Set)
		 *
		 * @param fn the transition function
		 * @return a new <em>delta</em> function
		 * @param <ST> the state type
		 * @param <SY> the symbol (signal) type
		 */
		static <ST extends State, SY extends Symbol>
		Delta<ST, SY> of(BiFunction<? super ST, ? super Signal, Optional<ST>> fn) {
			requireNonNull(fn);

			return new Delta<>() {
				@Override
				public Optional<ST> apply(ST current, SY symbol) {
					return fn.apply(current, symbol);
				}
				@Override
				public Optional<ST> apply(ST current, Event<SY> event) {
					return fn.apply(current, event)
						.or(() -> fn.apply(current, event.kind()));
				}
			};
		}

		static <ST extends State, SY extends Symbol>
		Delta<ST, SY> of(Function<? super StateSignal<ST, ? extends Signal>, Optional<ST>> fn) {
			requireNonNull(fn);

			return of((state, signal) -> fn.apply(new StateSignal<>(state, signal)));
		}

	}

	/**
	 * Combines a {@code state} with a {@code signal} associated to that
	 * {@code state},
	 *
	 * @param state the (current) state
	 * @param signal the signal (on that state)
	 * @param <ST> the state type
	 * @param <SI> the signal type
	 */
	public record StateSignal<ST extends State, SI extends Signal>(ST state, SI signal) {
		public StateSignal {
			requireNonNull(state);
			requireNonNull(signal);
		}
	}

	/**
	 * Defines a state-transition triple {@code (s1, e, s2)}.
	 *
	 * @param before the current state
	 * @param signal the signal (event) that is triggering (or triggered) the
	 *        transition
	 * @param after the transitioned state
	 * @param <ST> the state type
	 * @param <SI> the signal type
	 */
	public record Transition<ST extends State, SI extends Signal>(
		ST before,
		SI signal,
		ST after
	) {
		public Transition {
			requireNonNull(before);
			requireNonNull(signal);
			requireNonNull(after);
		}
	}

	/* *************************************************************************
	 * Static methods for working with FSMs and event streams.
	 * ************************************************************************/

	/**
	 * Return a gatherer which enriches a signal stream with the states, defined
	 * by the given state machine, {@code fsm}. The gatherer ignores invalid
	 * signal (transitions) and stops when a final state, as defined by the
	 * {@code fsm}, has been reached.
	 *
	 * @param stepper the stepper used for gathering the transitions.
	 * @return a new transition gatherer
	 * @param <ST> the state type
	 * @param <SI> the symbol (signal) type
	 */
	public static <ST extends State, SI extends Signal>
	Gatherer<SI, ?, Transition<ST, SI>>
	transitions(Supplier<? extends Stepper<ST, SI>> stepper) {
		requireNonNull(stepper);

		return Gatherer.ofSequential(
			stepper,
			(stpr, signal, downstream) -> {
				final var transition = stpr.next(signal);
				transition.ifPresent(downstream::push);
				return !stpr.isFinished();
			}
		);
	}

}
