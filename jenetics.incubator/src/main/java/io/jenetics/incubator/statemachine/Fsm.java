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

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;

/**
 * Definition of a <a href="https://en.wikipedia.org/wiki/Finite-state_machine#Mathematical_model">
 *     Finit State Machine</a>.
 *
 * @param symbols the input alphabet (a finite non-empty set of symbols)
 * @param states the finite non-empty set of states
 * @param start the initial state, an element of {@link #states()}
 * @param finals the set of final states, a (possibly empty) subset of
 *        {@link #states()}
 * @param delta the state-transition function
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.1
 * @since 9.1
 */
public record Fsm(
	Set<? extends Symbol> symbols,
	Set<? extends State> states,
	State start,
	Set<? extends State> finals,
	Delta delta
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

	public Fsm(
		Set<? extends Symbol> symbols,
		Set<? extends State> states,
		State start,
		Set<? extends State> finals,
		Set<? extends Transition<? extends Symbol>> transitions
	) {
		this(symbols, states, start, finals, Transition.toDelta(transitions));
	}

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
	 * Interface for FSM transition events. Events may hold additional payload.
	 */
	public non-sealed interface Event extends Signal {

		/**
		 * Return the symbol, this event belongs to.
		 *
		 * @return the event symbol
		 */
		Symbol kind();
	}

	@FunctionalInterface
	public interface Delta {
		Optional<State> apply(State state, Symbol symbol);
	}

	/**
	 * Defines a state-transition triple.
	 *
	 * @param before the current state
	 * @param event the event that is triggering (or triggered) the transition
	 * @param after the transitioned state
	 * @param <T> the event type
	 */
	public record Transition<T extends Signal>(State before, T event, State after) {
		public Transition {
			requireNonNull(before);
			requireNonNull(event);
			requireNonNull(after);
		}

		public static Delta
		toDelta(Collection<? extends Transition<? extends Symbol>> transitions) {
			record StateSymbol(State state, Symbol symbol) {}

			final Map<StateSymbol, State> map = transitions.stream()
				.collect(Collectors.toMap(
					t -> new StateSymbol(t.before(), t.event()),
					Transition::after
				));

			return (state, symbol) -> Optional.ofNullable(
				map.get(new StateSymbol(state, symbol))
			);
		}
	}

	/**
	 * The event subscriber which is called for every new event being published.
	 */
	public interface EventSubscriber {

		/**
		 * This method is called for every event.
		 *
		 * @param event the event object, which triggers the state transition
		 * @param before the FSM state before the transition
		 * @param after the FSM state after the transition
		 */
		void onEvent(Event event, State before, State after);

		/**
		 * This method is called for invalid events, for events where no state
		 * transition is defined.
		 *
		 * @param event the event object, which triggers the state transition
		 * @param state the FSM state before the transition
		 * @throws IllegalStateException always. Implementer may override this
		 *         method and handle invalid events differently.
		 */
		default void onInvalidEvent(Event event, State state) {
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
		default void onAfterFinishEvent(Event event, State state) {
			throw new IllegalStateException(
				"Illegal event %s after finish state %s.".formatted(event, state)
			);
		}

	}

	/**
	 * The event publisher for an FSM. It holds the state, which is updated for
	 * every published event, according the Finite State Machine {@link Fsm}.
	 */
	public static final class EventPublisher {

		private final Fsm fsm;
		private final EventSubscriber subscriber;

		private final Executor executor;
		private final Object lock = new Object() {};

		private State state;

		public EventPublisher(
			final Fsm fsm,
			final State start,
			final EventSubscriber subscriber,
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

		public EventPublisher(Fsm fsm, State start, EventSubscriber subscriber) {
			this(fsm, start, subscriber, Runnable::run);
		}

		/**
		 * Return the current state of the runner.
		 *
		 * @return the current state of the runner
		 */
		public State state() {
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
		 * @return {@code true} if the event has been processed, {@code false}
		 *         otherwise. If {@code false} is returned, one of the final
		 *         states has been reached.
		 */
		public boolean submit(Event event) {
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
					return false;
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
					return !isFinished();
				}
			}
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
	 * @return a new states gatherer
	 */
	public static Gatherer<Event, ?, Transition<Event>>
	transitions(Fsm fsm, State start) {
		requireNonNull(fsm);
		requireNonNull(start);

		record StateHolder(AtomicReference<State> value) {}

		return Gatherer.ofSequential(
			() -> new StateHolder(new AtomicReference<>(start)),

			Gatherer.Integrator.of((state, event, downstream) -> {
				var after = fsm.delta().apply(state.value.get(), event.kind());
				after.ifPresent(s -> {
					downstream.push(new Transition<>(state.value.get(), event, s));
					state.value.set(s);
				});

				return !fsm.finals().contains(after.orElse(state.value.get()));
			})
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
	 * @return a new states gatherer
	 */
	public static Gatherer<Event, ?, Transition<Event>> transitions(Fsm fsm) {
		return transitions(fsm, fsm.start());
	}

}
