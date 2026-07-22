package io.jenetics.incubator.statemachine;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;

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
 */
public record Fsm(
	Set<Symbol> symbols,
	Set<State> states,
	State start,
	Set<State> finals,
	BiFunction<? super State, ? super Symbol, ? extends State> delta
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
		if (states.contains(start)) {
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

	/**
	 * Interface for FSM symbols. A set of symbols form the alphabet of the FSM.
	 */
	public interface Symbol {
		/**
		 * Return the symbol name.
		 *
		 * @return the symbol name
		 */
		String name();
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
	}

	/**
	 * The runner class for an FSM.
	 */
	public static final class Runner {

		/**
		 * Interface for FSM transition events. Events may hold additional payload.
		 */
		public interface Event {
			/**
			 * Return the symbol, this event belongs to.
			 *
			 * @return the event symbol
			 */
			Symbol symbol();
		}

		/**
		 * The event listener which is called for every state transition.
		 */
		public interface EventListener {
			/**
			 * This method is called for every event.
			 *
			 * @param source the runner object which called {@code this} listener
			 * @param event the event object, which triggers the state transition
			 * @param prev the FSM state before the transition
			 * @param next the FSM state after the transition
			 */
			void update(Runner source, Event event, State prev, State next);

			/**
			 * This method is called for receiving events if the state is already
			 * final.
			 *
			 * @param source
			 * @param event
			 */
			void finished(Runner source, Event event);
		}

		private final Fsm fsm;
		private final EventListener listener;
		private final Executor executor;
		private final Object lock = new Object() {};

		private State state;

		public Runner(Fsm fsm, EventListener listener, Executor executor) {
			this.fsm = requireNonNull(fsm);
			this.listener = requireNonNull(listener);
			this.executor = requireNonNull(executor);
			this.state = fsm.start();
		}

		/**
		 * Return the current state of the runner.
		 *
		 * @return the current statte of the runner
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
		public boolean next(Event event) {
			synchronized (lock) {
				if (isFinished()) {
					listener.finished(this, event);
					return false;
				} else {
					final var next = fsm.delta.apply(state, event.symbol());
					listener.update(this, event, state, next);
					state = next;
					return !isFinished();
				}
			}
		}

	}


}
