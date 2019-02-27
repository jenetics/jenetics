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
package io.jenetics.engine;

import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.Hashes.hash;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

/**
 * This class contains timing information about one evolution step.
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public final class EvolutionDurations
	implements
		Comparable<EvolutionDurations>,
		Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constant for zero evolution durations.
	 */
	public static final EvolutionDurations ZERO = EvolutionDurations.of(
		Duration.ZERO,
		Duration.ZERO,
		Duration.ZERO,
		Duration.ZERO,
		Duration.ZERO,
		Duration.ZERO,
		Duration.ZERO
	);

	private final Duration _offspringSelectionDuration;
	private final Duration _survivorsSelectionDuration;
	private final Duration _offspringAlterDuration;
	private final Duration _offspringFilterDuration;
	private final Duration _survivorFilterDuration;
	private final Duration _evaluationDuration;
	private final Duration _evolveDuration;

	EvolutionDurations(
		final Duration offspringSelectionDuration,
		final Duration survivorsSelectionDuration,
		final Duration offspringAlterDuration,
		final Duration offspringFilterDuration,
		final Duration survivorFilterDuration,
		final Duration evaluationDuration,
		final Duration evolveDuration
	) {
		_offspringSelectionDuration = requireNonNull(offspringSelectionDuration);
		_survivorsSelectionDuration = requireNonNull(survivorsSelectionDuration);
		_offspringAlterDuration = requireNonNull(offspringAlterDuration);
		_offspringFilterDuration = requireNonNull(offspringFilterDuration);
		_survivorFilterDuration = requireNonNull(survivorFilterDuration);
		_evaluationDuration = requireNonNull(evaluationDuration);
		_evolveDuration = requireNonNull(evolveDuration);
	}

	/**
	 * Return the duration needed for selecting the offspring population.
	 *
	 * @return the duration needed for selecting the offspring population
	 */
	public Duration getOffspringSelectionDuration() {
		return _offspringSelectionDuration;
	}

	/**
	 * Return the duration needed for selecting the survivors population.
	 *
	 * @return the duration needed for selecting the survivors population
	 */
	public Duration getSurvivorsSelectionDuration() {
		return _survivorsSelectionDuration;
	}

	/**
	 * Return the duration needed for altering the offspring population.
	 *
	 * @return the duration needed for altering the offspring population
	 */
	public Duration getOffspringAlterDuration() {
		return _offspringAlterDuration;
	}

	/**
	 * Return the duration needed for removing and replacing invalid offspring
	 * individuals.
	 *
	 * @return the duration needed for removing and replacing invalid offspring
	 *         individuals
	 */
	public Duration getOffspringFilterDuration() {
		return _offspringFilterDuration;
	}

	/**
	 * Return the duration needed for removing and replacing old and invalid
	 * survivor individuals.
	 *
	 * @return the duration needed for removing and replacing old and invalid
	 *         survivor individuals
	 */
	public Duration getSurvivorFilterDuration() {
		return _survivorFilterDuration;
	}

	/**
	 * Return the duration needed for evaluating the fitness function of the new
	 * individuals.
	 *
	 * @return the duration needed for evaluating the fitness function of the new
	 *         individuals
	 */
	public Duration getEvaluationDuration() {
		return _evaluationDuration;
	}

	/**
	 * Return the duration needed for the whole evolve step.
	 *
	 * @return the duration needed for the whole evolve step
	 */
	public Duration getEvolveDuration() {
		return _evolveDuration;
	}

	/**
	 * Returns a copy of this duration with the specified duration added.
	 * <p>
	 * This instance is immutable and unaffected by this method call.
	 *
	 * @param other the duration to add
	 * @return a {@code EvolutionDurations} based on this duration with the
	 *         specified duration added
	 * @throws NullPointerException if the {@code other} duration is {@code null}
	 * @throws ArithmeticException if numeric overflow occurs
	 */
	public EvolutionDurations plus(final EvolutionDurations other) {
		requireNonNull(other);
		return of(
			_offspringSelectionDuration.plus(other._offspringSelectionDuration),
			_survivorsSelectionDuration.plus(other._survivorsSelectionDuration),
			_offspringAlterDuration.plus(other._offspringAlterDuration),
			_offspringFilterDuration.plus(other._offspringFilterDuration),
			_survivorFilterDuration.plus(other._survivorFilterDuration),
			_evaluationDuration.plus(other._evaluationDuration),
			_evolveDuration.plus(other._evolveDuration)
		);
	}

	EvolutionDurations plusEvaluation(final Duration duration) {
		return of(
			_offspringSelectionDuration,
			_survivorsSelectionDuration,
			_offspringAlterDuration,
			_offspringFilterDuration,
			_survivorFilterDuration,
			_evaluationDuration.plus(duration),
			_evolveDuration
		);
	}

	EvolutionDurations plusEvolve(final Duration duration) {
		return of(
			_offspringSelectionDuration,
			_survivorsSelectionDuration,
			_offspringAlterDuration,
			_offspringFilterDuration,
			_survivorFilterDuration,
			_evaluationDuration,
			_evolveDuration.plus(duration)
		);
	}

	/**
	 * Compares two durations objects. Only the {@link #getEvolveDuration()}
	 * property is taken into account for the comparison.
	 *
	 * @param other the other durations object this object is compared with
	 * @return a integer smaller/equal/greater than 0 if the
	 *         {@link #getEvolveDuration()} property of {@code this} object is
	 *         smaller/equal/greater than the corresponding property of the
	 *         {@code other} project.
	 */
	@Override
	public int compareTo(final EvolutionDurations other) {
		return _evolveDuration.compareTo(other._evolveDuration);
	}

	@Override
	public int hashCode() {
		return
			hash(_offspringSelectionDuration,
			hash(_survivorFilterDuration,
			hash(_offspringAlterDuration,
			hash(_offspringFilterDuration,
			hash(_survivorsSelectionDuration,
			hash(_evaluationDuration,
			hash(_evolveDuration)))))));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof EvolutionDurations &&
			Objects.equals(_offspringSelectionDuration,
				((EvolutionDurations)obj)._offspringSelectionDuration) &&
			Objects.equals(_survivorsSelectionDuration,
				((EvolutionDurations)obj)._survivorsSelectionDuration) &&
			Objects.equals(_offspringAlterDuration,
				((EvolutionDurations)obj)._offspringAlterDuration) &&
			Objects.equals(_offspringFilterDuration,
				((EvolutionDurations)obj)._offspringFilterDuration) &&
			Objects.equals(_survivorFilterDuration,
				((EvolutionDurations)obj)._survivorFilterDuration) &&
			Objects.equals(_evaluationDuration,
				((EvolutionDurations)obj)._evaluationDuration) &&
			Objects.equals(_evolveDuration,
				((EvolutionDurations)obj)._evolveDuration);
	}

	/**
	 * Return an new {@code EvolutionDurations} object with the given values.
	 *
	 * @param offspringSelectionDuration the duration needed for selecting the
	 *        offspring population
	 * @param survivorsSelectionDuration the duration needed for selecting the
	 *        survivors population
	 * @param offspringAlterDuration the duration needed for altering the
	 *        offspring population
	 * @param offspringFilterDuration the duration needed for removing and
	 *        replacing invalid offspring individuals
	 * @param survivorFilterDuration the duration needed for removing and
	 *        replacing old and invalid survivor individuals
	 * @param evaluationDuration the duration needed for evaluating the fitness
	 *        function of the new individuals
	 * @param evolveDuration the duration needed for the whole evolve step
	 * @return an new durations object
	 * @throws NullPointerException if one of the arguments is
	 *         {@code null}
	 */
	public static EvolutionDurations of(
		final Duration offspringSelectionDuration,
		final Duration survivorsSelectionDuration,
		final Duration offspringAlterDuration,
		final Duration offspringFilterDuration,
		final Duration survivorFilterDuration,
		final Duration evaluationDuration,
		final Duration evolveDuration
	) {
		return new EvolutionDurations(
			offspringSelectionDuration,
			survivorsSelectionDuration,
			offspringAlterDuration,
			offspringFilterDuration,
			survivorFilterDuration,
			evaluationDuration,
			evolveDuration
		);
	}

}
