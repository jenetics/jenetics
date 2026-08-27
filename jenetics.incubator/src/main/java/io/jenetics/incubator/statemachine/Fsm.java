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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;

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
				.map(to -> Transition.of(step.getKey(), step.getValue(), to))
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
	 * @param <ST> the state type
	 * @param <SI> the signal (event) type
	 */
	public interface Transition<ST extends State, SI extends Signal> {

		/**
		 * return the state before the transition.
		 *
		 * @return the state before the transition
		 */
		ST before();

		/**
		 * Return the signal that is triggering (or triggered) the transition.
		 *
		 * @return the signal that is triggering (or triggered) the transition
		 */
		SI signal();

		/**
		 * return the state after the transition.
		 *
		 * @return the state after the transition
		 */
		ST after();

		static <ST extends State, SI extends Signal>
		Transition<ST, SI> of(ST before, SI signal, ST after) {
			record SimpleTransition<ST extends State, SI extends Signal>(
				ST before,
				SI signal,
				ST after
			)
				implements Transition<ST, SI>
			{
				public SimpleTransition {
					requireNonNull(before);
					requireNonNull(signal);
					requireNonNull(after);
				}
			}

			return new SimpleTransition<>(before, signal, after);
		}
	}


	/* *************************************************************************
	 * Classes and interfaces for processing/executing the FSM.
	 * ************************************************************************/

	/**
	 * Common interface for signal submitter.
	 *
	 * @param <SY> the symbol (signal) type
	 * @param <E> the signal type
	 */
	public interface Submitter<SY extends Symbol, E extends Event<SY>> {
		void submit(E event);
	}

	/**
	 * The signal subscriber which is called for every new signal being published.
	 *
	 * @param <ST> the state type
	 * @param <SY> the symbol (signal) type
	 * @param <E> the signal type
	 */
	public interface EventSubscriber<
		ST extends State,
		SY extends Symbol,
		E extends Event<SY>
	> {

		/**
		 * This method is called for every signal.
		 *
		 * @param event the signal object, which triggers the state transition
		 * @param before the FSM state before the transition
		 * @param after the FSM state after the transition
		 */
		void onEvent(E event, ST before, ST after);

		/**
		 * This method is called for invalid events, for events where no state
		 * transition is defined.
		 *
		 * @param event the signal object, which triggers the state transition
		 * @param state the FSM state before the transition
		 * @throws IllegalStateException always. Implementer may override this
		 *         method and handle invalid events differently.
		 */
		default void onInvalidEvent(E event, ST state) {
			throw new IllegalStateException(
				"Illegal signal %s for state %s.".formatted(event, state)
			);
		}

		/**
		 * This method is called for all events after the FSM state is already
		 * a finished state.
		 *
		 * @param event the signal object, which triggers the state transition
		 * @param state the finished state
		 * @throws IllegalStateException always. Implementer may override this
		 *         method and handle events after the finished state differently.
		 */
		default void onAfterFinishEvent(E event, ST state) {
			throw new IllegalStateException(
				"Illegal signal %s after finish state %s.".formatted(event, state)
			);
		}

	}

	/**
	 * The signal publisher for an FSM. It holds the state, which is updated for
	 * every published signal, according the Finite State Machine {@link Fsm}.
	 *
	 * @param <ST> the state type
	 * @param <SY> the symbol (signal) type
	 * @param <E> the signal type
	 */
	public static final class EventPublisher<
		ST extends State,
		SY extends Symbol,
		E extends Event<SY>
	>
		implements Submitter<SY, E>
	{

		private final Stepper<ST, SY> stepper;
		private final EventSubscriber<ST, SY, E> subscriber;

		private final Executor executor;

		public EventPublisher(
			final Fsm<ST, SY> fsm,
			final ST start,
			final EventSubscriber<ST, SY, E> subscriber,
			final Executor executor
		) {
			this.stepper = new Stepper<>(fsm, start);
			this.subscriber = requireNonNull(subscriber);
			this.executor = requireNonNull(executor);
		}

		public EventPublisher(
			final Fsm<ST, SY> fsm,
			final ST start,
			final EventSubscriber<ST, SY, E> subscriber
		) {
			this(fsm, start, subscriber, Runnable::run);
		}

		/**
		 * Return the current state of the runner.
		 *
		 * @return the current state of the runner
		 */
		public ST current() {
			return stepper.state();
		}

		public boolean isFinished() {
			return stepper.isFinished();
		}

		/**
		 * Consumer the <em>next</em> signal. {@code true} is returned, if the
		 * signal has been processed and the final state hasn't been reached yet.
		 *
		 * @param event the transitioning signal
		 */
		@Override
		public void submit(E event) {
			requireNonNull(event);

			if (isFinished()) {
				executor.execute(() -> subscriber
					.onAfterFinishEvent(event, current()));
			} else {
				switch (stepper.next(event.kind())) {
					case Step.Valid(var before, _, var after) -> executor
						.execute(() -> subscriber.onEvent(event, before, after));
					case Step.Invalid(var before, _) -> executor
						.execute(() -> subscriber.onInvalidEvent(event, before));
				}
			}
		}
	}


	public static final class FlowPublisher<
		ST extends State,
		SY extends Symbol,
		E extends Event<SY>
	>
		implements Flow.Publisher<Transition<ST, E>>
	{
		@Override
		public void subscribe(Flow.Subscriber<? super Transition<ST, E>> subscriber) {
		}
	}


	/* *************************************************************************
	 * Static methods for working with FSMs.
	 * ************************************************************************/


	public static <ST extends State, SY extends Symbol>
	Gatherer<SY, ?, Step<ST, SY>> steps(Fsm<ST, SY> fsm, ST start) {
		requireNonNull(fsm);
		requireNonNull(start);

		return Gatherer.ofSequential(
			() -> new Stepper<>(fsm, start),
			(stepper, signal, downstream) -> {
				downstream.push(stepper.next(signal));
				return !stepper.isFinished();
			}
		);
	}

	public static <ST extends State, SY extends Symbol>
	Gatherer<SY, ?, Step<ST, SY>> steps(Fsm<ST, SY> fsm) {
		return steps(fsm, fsm.start());
	}

	/**
	 * Return a gatherer which enriches an signal stream with the states, defined
	 * by the given state machine, {@code fsm}. The gatherer ignores invalid
	 * signal (transitions) and stops when a final state, as defined by the
	 * {@code fsm}, has been reached.
	 *
	 * @param fsm the state machine which defines the before and after state of
	 *        an signal stream
	 * @param start the start state
	 * @param <ST> the state type
	 * @param <SY> the symbol (signal) type
	 * @param <E> the signal type
	 * @return a new states gatherer
	 */
	public static <ST extends State, SY extends Symbol, E extends Event<SY>>
	Gatherer<E, ?, Transition<ST, E>>
	transitions(Fsm<ST, SY> fsm, ST start) {
		requireNonNull(fsm);
		requireNonNull(start);

		return Gatherer.ofSequential(
			() -> new AtomicReference<>(start),
			(state, event, downstream) -> {
				var after = fsm.delta().apply(state.get(), event.kind());
				after.ifPresent(aftr -> {
					downstream.push(Transition.of(state.get(), event, aftr));
					state.set(aftr);
				});

				return !fsm.finals().contains(after.orElse(state.get()));
			}
		);
	}

	/**
	 * Return a gatherer which enriches an signal stream with the states, defined
	 * by the given state machine, {@code fsm}. The gatherer ignores invalid
	 * signal (transitions) and stops when a final state, as defined by the
	 * {@code fsm}, has been reached.
	 *
	 * @param fsm the state machine which defines the before and after state of
	 *        an signal stream
	 * @param <ST> the state type
	 * @param <SY> the symbol (signal) type
	 * @param <E> the signal type
	 * @return a new states gatherer
	 */
	public static <ST extends State, SY extends Symbol, E extends Event<SY>>
	Gatherer<E, ?, Transition<ST, E>> transitions(Fsm<ST, SY> fsm) {
		return transitions(fsm, fsm.start());
	}

}
