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
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.readLong;
import static io.jenetics.internal.util.SerialIO.writeInt;
import static io.jenetics.internal.util.SerialIO.writeLong;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
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
 * @version 6.0
 */
public final /*record*/ class EvolutionDurations
	implements
		Comparable<EvolutionDurations>,
		Serializable
{
	private static final long serialVersionUID = 2L;

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
	public Duration offspringSelectionDuration() {
		return _offspringSelectionDuration;
	}

	/**
	 * Return the duration needed for selecting the survivors population.
	 *
	 * @return the duration needed for selecting the survivors population
	 */
	public Duration survivorsSelectionDuration() {
		return _survivorsSelectionDuration;
	}

	/**
	 * Return the duration needed for altering the offspring population.
	 *
	 * @return the duration needed for altering the offspring population
	 */
	public Duration offspringAlterDuration() {
		return _offspringAlterDuration;
	}

	/**
	 * Return the duration needed for removing and replacing invalid offspring
	 * individuals.
	 *
	 * @return the duration needed for removing and replacing invalid offspring
	 *         individuals
	 */
	public Duration offspringFilterDuration() {
		return _offspringFilterDuration;
	}

	/**
	 * Return the duration needed for removing and replacing old and invalid
	 * survivor individuals.
	 *
	 * @return the duration needed for removing and replacing old and invalid
	 *         survivor individuals
	 */
	public Duration survivorFilterDuration() {
		return _survivorFilterDuration;
	}

	/**
	 * Return the duration needed for evaluating the fitness function of the new
	 * individuals.
	 *
	 * @return the duration needed for evaluating the fitness function of the new
	 *         individuals
	 */
	public Duration evaluationDuration() {
		return _evaluationDuration;
	}

	/**
	 * Return the duration needed for the whole evolve step.
	 *
	 * @return the duration needed for the whole evolve step
	 */
	public Duration evolveDuration() {
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
	 * Compares two durations objects. Only the {@link #evolveDuration()}
	 * property is taken into account for the comparison.
	 *
	 * @param other the other durations object this object is compared with
	 * @return a integer smaller/equal/greater than 0 if the
	 *         {@link #evolveDuration()} property of {@code this} object is
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


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.EVOLUTION_DURATIONS, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final ObjectOutput out) throws IOException {
		writeDuration(_offspringSelectionDuration, out);
		writeDuration(_survivorsSelectionDuration, out);
		writeDuration(_offspringAlterDuration, out);
		writeDuration(_offspringFilterDuration, out);
		writeDuration(_survivorFilterDuration, out);
		writeDuration(_evaluationDuration, out);
		writeDuration(_evolveDuration, out);
	}

	private static void writeDuration(final Duration duration, final DataOutput out)
		throws IOException
	{
		writeLong(duration.getSeconds(), out);
		writeInt(duration.getNano(), out);
	}

	static EvolutionDurations read(final ObjectInput in) throws IOException {
		return new EvolutionDurations(
			readDuration(in),
			readDuration(in),
			readDuration(in),
			readDuration(in),
			readDuration(in),
			readDuration(in),
			readDuration(in)
		);
	}

	private static Duration readDuration(final DataInput in) throws IOException {
		final long seconds = readLong(in);
		final int nanos = readInt(in);
		return Duration.ofSeconds(seconds, nanos);
	}

}
