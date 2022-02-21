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
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.readLong;
import static io.jenetics.internal.util.SerialIO.writeInt;
import static io.jenetics.internal.util.SerialIO.writeLong;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;

/**
 * This class contains timing information about one evolution step.
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
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 7.0
 */
public record EvolutionDurations(
	Duration offspringSelectionDuration,
	Duration survivorsSelectionDuration,
	Duration offspringAlterDuration,
	Duration offspringFilterDuration,
	Duration survivorFilterDuration,
	Duration evaluationDuration,
	Duration evolveDuration
)
	implements
		Comparable<EvolutionDurations>,
		Serializable
{
	@Serial
	private static final long serialVersionUID = 3L;

	/**
	 * Constant for zero evolution durations.
	 */
	public static final EvolutionDurations ZERO = new EvolutionDurations(
		Duration.ZERO,
		Duration.ZERO,
		Duration.ZERO,
		Duration.ZERO,
		Duration.ZERO,
		Duration.ZERO,
		Duration.ZERO
	);

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
		return new EvolutionDurations(
			offspringSelectionDuration.plus(other.offspringSelectionDuration),
			survivorsSelectionDuration.plus(other.survivorsSelectionDuration),
			offspringAlterDuration.plus(other.offspringAlterDuration),
			offspringFilterDuration.plus(other.offspringFilterDuration),
			survivorFilterDuration.plus(other.survivorFilterDuration),
			evaluationDuration.plus(other.evaluationDuration),
			evolveDuration.plus(other.evolveDuration)
		);
	}

	EvolutionDurations plusEvaluation(final Duration duration) {
		return new EvolutionDurations(
			offspringSelectionDuration,
			survivorsSelectionDuration,
			offspringAlterDuration,
			offspringFilterDuration,
			survivorFilterDuration,
			evaluationDuration.plus(duration),
			evolveDuration
		);
	}

	EvolutionDurations plusEvolve(final Duration duration) {
		return new EvolutionDurations(
			offspringSelectionDuration,
			survivorsSelectionDuration,
			offspringAlterDuration,
			offspringFilterDuration,
			survivorFilterDuration,
			evaluationDuration,
			evolveDuration.plus(duration)
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
		return evolveDuration.compareTo(other.evolveDuration);
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	void write(final ObjectOutput out) throws IOException {
		writeDuration(offspringSelectionDuration, out);
		writeDuration(survivorsSelectionDuration, out);
		writeDuration(offspringAlterDuration, out);
		writeDuration(offspringFilterDuration, out);
		writeDuration(survivorFilterDuration, out);
		writeDuration(evaluationDuration, out);
		writeDuration(evolveDuration, out);
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
