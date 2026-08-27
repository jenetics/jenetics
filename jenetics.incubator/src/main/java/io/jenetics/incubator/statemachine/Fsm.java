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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;

/**
 * Definition of a <a href="https://en.wikipedia.org/wiki/Finite-state_machine#Mathematical_model">
 *     Finit State Machine</a>.
 * @implNote
 * The {@link Fsm} class is immutable and thread safe and can be shared between
 * different threads and processors (signal publisher).
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

	/**
	 * Interface for FSM symbols. A set of symbols form the alphabet of the FSM.
	 */
	public non-sealed interface Symbol extends Signal {

		/**
		 * Return the symbol name.
		 *
		 * @return the symbol name
		 */
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
	}

	/**
	 * Interface for the FSM states.
	 */
	public interface State {

		/**
		 * Return the state name
		 *
		 * @return the name of the state
		 */
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

	/**
	 * Interface for a symbol (event) stepper.
	 *
	 * @param <ST> the state type
	 * @param <SI> the symbol (signal) type
	 */
	public interface Stepper<ST extends State, SI extends Signal> {

		/**
		 * Return {@code true} if the current state is an element of the
		 * {@link Fsm#finals()} states.
		 *
		 * @return {@code true} if the current state is an element of the
		 * {@link Fsm#finals()} states, {@code false} otherwise.
		 */
		boolean isFinished();

		/**
		 * Moves the current state to the next state by applying the given
		 * {@code signal}.
		 *
		 * @param signal the signal which moves the current step to the next step
		 * @return the transition, if any
		 * @throws IllegalStateException if the {@link #isFinished()} is {@code true}
		 * @throws IllegalArgumentException if the given {@code signal} is not
		 *         an element of {@link Fsm#alphabet()}
		 */
		Optional<Fsm.Transition<ST, SI>> next(SI signal);

	}

	/* *************************************************************************
	 * Static methods for working with FSMs.
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
	transitions(Supplier<Stepper<ST, SI>> stepper) {
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
