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
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;

/**
 * Definition of a <a href="https://en.wikipedia.org/wiki/Finite-state_machine#Mathematical_model">
 *     Finit State Machine</a>.
 * The {@link Fsm} is modeled as a quintuple (record) {@code (Σ, S, s0, δ, F)},
 * where:
 * <ul>
 *     <li><b>Σ</b> is the non-empty alphabet of symbols (signals);</li>
 *     <li><b>S</b> is the finit non-empty set of states;</li>
 *     <li><b>s0</b> is the initial state, which is an element of S;</li>
 *     <li><b>δ</b> is the transition function: δ: S x Σ -> S;</li>
 *     <li><b>F</b> is the possible empty set of final states, which are
 *         elements of S.</li>
 * </ul>
 *
 * A {@link Fsm} instance is usually created as static constant.
 *
 * <h1>Defining state machine</h1>
 * {@snippet lang=java:
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
 * The <em>execution</em> of the state machine is done by the {@link Stepper}
 * implementations, which also holds the current state of the state machines.
 * {@link Stepper} instances are usually not shared between different threads.
 * On top of the {@link Stepper}, the FSM can be run in a reactive manner using
 * the {@link SignalPublisher} and {@link SignalSubscriber} classes. The
 * {@link OberservableStepper} adapter lets you execute the FSM in an event-based
 * kind.
 *
 * <h1>Execute action on state transitions</h1>
 * The immutable FSM can be used to convert a stream of events into a stream of
 * state transitions. It is then possible to execute actions on these transitions.
 * {@snippet lang=java:
 * events.stream()
 *     .gather(Fsm.transitions(() -> new SymbolStepper<>(FSM)))
 *     .forEach(IO::println);
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
	Set<ST> finals,
	Delta<ST, SY> delta
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

			return new  SimpleSymbol(name);
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
		 * Creates a <em>delta</em> function from the given set of
		 * {@code transitions}.
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

			record StateSymbol<ST extends State, SY extends Symbol>(
				ST state,
				SY symbol
			) {}

			final Map<StateSymbol<ST, SY>, List<Transition<ST, SY>>> map =
				transitions.stream()
					.collect(groupingBy(t -> new StateSymbol<>(t.before(), t.signal())));

			final List<StateSymbol<ST, SY>> duplicates = map.entrySet().stream()
				.filter(t -> t.getValue().size() > 1)
				.map(Map.Entry::getKey)
				.toList();

			if (!duplicates.isEmpty()) {
				throw new IllegalArgumentException(
					"Found ambiguous transitions: %s.".formatted(
						duplicates.stream()
							.map(ss -> "(%s, %s)".formatted(ss.state, ss.symbol))
							.collect(Collectors.joining(", "))
					)
				);
			}

			return (state, symbol) -> Optional
				.ofNullable(map.get(new StateSymbol<>(state, symbol)))
				.map(t -> t.getFirst().after());
		}

		/**
		 * Creates a <em>delta</em> function from the given set of
		 * {@code transitions}.
		 *
		 * @see #of(Set)
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
