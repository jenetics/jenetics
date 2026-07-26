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
 * different threads and processors (event publisher).
 *
 * @param symbols the input alphabet (a finite non-empty set of symbols)
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
	Set<SY> symbols,
	Set<ST> states,
	ST start,
	Set<ST> finals,
	Delta<ST, SY> delta
) {

	public Fsm {
		symbols = Set.copyOf(symbols);
		states = Set.copyOf(states);
		requireNonNull(start);
		finals = Set.copyOf(finals);
		requireNonNull(delta);

		if (symbols.isEmpty()) {
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
		final var missing = finals.stream()
			.filter(not(states::contains))
			.toList();
		if (!missing.isEmpty()) {
			throw new IllegalArgumentException(
				"Final states %s are not part of the available states, %s."
					.formatted(missing, states)
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
					.collect(groupingBy(t -> new StateSymbol<>(t.before(), t.event())));

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
	 * Defines a state-transition triple.
	 *
	 * @param before the current state
	 * @param event the event that is triggering (or triggered) the transition
	 * @param after the transitioned state
	 * @param <ST> the state type
	 * @param <SI> the signal type
	 */
	public record Transition<ST extends State, SI extends Signal>(
		ST before,
		SI event,
		ST after
	) {
		public Transition {
			requireNonNull(before);
			requireNonNull(event);
			requireNonNull(after);
		}
	}


	/* *************************************************************************
	 * Classes and interfaces for processing/executing the FSM.
	 * ************************************************************************/

	/**
	 * Interface for FSM transition events. Events may hold additional payload.
	 *
	 * @param <SY> the symbol (signal) type
	 */
	public non-sealed interface Event<SY extends Symbol> extends Signal {

		/**
		 * Return the symbol, this event belongs to.
		 *
		 * @return the event symbol
		 */
		SY kind();
	}

	/**
	 * Common interface for event submitter.
	 *
	 * @param <SY> the symbol (signal) type
	 * @param <E> the event type
	 */
	public interface Submitter<SY extends Symbol, E extends Event<SY>> {
		void submit(E event);
	}

	public interface Stepper {

	}

	/**
	 * The event subscriber which is called for every new event being published.
	 *
	 * @param <ST> the state type
	 * @param <SY> the symbol (signal) type
	 * @param <E> the event type
	 */
	public interface EventSubscriber<
		ST extends State,
		SY extends Symbol,
		E extends Event<SY>
	> {

		/**
		 * This method is called for every event.
		 *
		 * @param event the event object, which triggers the state transition
		 * @param before the FSM state before the transition
		 * @param after the FSM state after the transition
		 */
		void onEvent(E event, ST before, ST after);

		/**
		 * This method is called for invalid events, for events where no state
		 * transition is defined.
		 *
		 * @param event the event object, which triggers the state transition
		 * @param state the FSM state before the transition
		 * @throws IllegalStateException always. Implementer may override this
		 *         method and handle invalid events differently.
		 */
		default void onInvalidEvent(E event, ST state) {
			throw new IllegalStateException(
				"Illegal event %s for state %s.".formatted(event, state)
			);
		}

		/**
		 * This method is called for all events after the FSM state is already
		 * a finished state.
		 *
		 * @param event the event object, which triggers the state transition
		 * @param state the finished state
		 * @throws IllegalStateException always. Implementer may override this
		 *         method and handle events after the finished state differently.
		 */
		default void onAfterFinishEvent(E event, ST state) {
			throw new IllegalStateException(
				"Illegal event %s after finish state %s.".formatted(event, state)
			);
		}

	}

	/**
	 * The event publisher for an FSM. It holds the state, which is updated for
	 * every published event, according the Finite State Machine {@link Fsm}.
	 *
	 * @param <ST> the state type
	 * @param <SY> the symbol (signal) type
	 * @param <E> the event type
	 */
	public static final class EventPublisher<
		ST extends State,
		SY extends Symbol,
		E extends Event<SY>
	>
		implements Submitter<SY, E>
	{

		private final Fsm<ST, SY> fsm;
		private final EventSubscriber<ST, SY, E> subscriber;

		private final Executor executor;
		private final Object lock = new Object() {};

		private ST state;

		public EventPublisher(
			final Fsm<ST, SY> fsm,
			final ST start,
			final EventSubscriber<ST, SY, E> subscriber,
			final Executor executor
		) {
			this.fsm = requireNonNull(fsm);
			this.subscriber = requireNonNull(subscriber);
			this.state = requireNonNull(start);
			this.executor = requireNonNull(executor);

			if (!fsm.states().contains(state)) {
				throw new IllegalArgumentException(
					"Initial state '%s' is not part of available states, %s."
						.formatted(state, fsm.states())
				);
			}
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
		public ST state() {
			synchronized (lock) {
				return state;
			}
		}

		public boolean isFinished() {
			synchronized (lock) {
				return fsm.finals().contains(state);
			}
		}

		/**
		 * Consumer the <em>next</em> event. {@code true} is returned, if the
		 * event has been processed and the final state hasn't been reached yet.
		 *
		 * @param event the transitioning event
		 */
		@Override
		public void submit(E event) {
			if (!fsm.symbols().contains(event.kind())) {
				throw new IllegalArgumentException(
					"Got event with unknown kind: " + event
				);
			}

			synchronized (lock) {
				if (isFinished()) {
					executor.execute(() ->
						subscriber.onAfterFinishEvent(event, state)
					);
				} else {
					final var next = fsm.delta.apply(state, event.kind());
					next.ifPresentOrElse(
						n -> executor.execute(() ->
								subscriber.onEvent(event, state, n)
						),
						() -> executor.execute(() ->
							subscriber.onInvalidEvent(event, state)
						)
					);

					state = next.orElse(state);
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

	/**
	 * Return a gatherer which enriches an event stream with the states, defined
	 * by the given state machine, {@code fsm}. The gatherer ignores invalid
	 * event (transitions) and stops when a final state, as defined by the
	 * {@code fsm}, has been reached.
	 *
	 * @param fsm the state machine which defines the before and after state of
	 *        an event stream
	 * @param start the start state
	 * @param <ST> the state type
	 * @param <SY> the symbol (signal) type
	 * @param <E> the event type
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
					downstream.push(new Transition<>(state.get(), event, aftr));
					state.set(aftr);
				});

				return !fsm.finals().contains(after.orElse(state.get()));
			}
		);
	}

	/**
	 * Return a gatherer which enriches an event stream with the states, defined
	 * by the given state machine, {@code fsm}. The gatherer ignores invalid
	 * event (transitions) and stops when a final state, as defined by the
	 * {@code fsm}, has been reached.
	 *
	 * @param fsm the state machine which defines the before and after state of
	 *        an event stream
	 * @param <ST> the state type
	 * @param <SY> the symbol (signal) type
	 * @param <E> the event type
	 * @return a new states gatherer
	 */
	public static <ST extends State, SY extends Symbol, E extends Event<SY>>
	Gatherer<E, ?, Transition<ST, E>> transitions(Fsm<ST, SY> fsm) {
		return transitions(fsm, fsm.start());
	}

}
