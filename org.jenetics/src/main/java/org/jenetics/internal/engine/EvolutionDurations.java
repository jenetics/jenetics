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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.engine;

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;
import java.time.Duration;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-19 $</em>
 */
public class EvolutionDurations implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Duration _offspringSelectionDuration;
	private final Duration _survivorsSelectionDuration;
	private final Duration _offspringAlterDuration;
	private final Duration _offspringFilterDuration;
	private final Duration _survivorFilterDuration;
	private final Duration _evaluationDuration;
	private final Duration _evolveDuration;

	private EvolutionDurations(
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

	public Duration getOffspringSelectionDuration() {
		return _offspringSelectionDuration;
	}

	public Duration getSurvivorsSelectionDuration() {
		return _survivorsSelectionDuration;
	}

	public Duration getOffspringAlterDuration() {
		return _offspringAlterDuration;
	}

	public Duration getOffspringFilterDuration() {
		return _offspringFilterDuration;
	}

	public Duration getSurvivorFilterDuration() {
		return _survivorFilterDuration;
	}

	public Duration getEvaluationDuration() {
		return _evaluationDuration;
	}

	public Duration getEvolveDuration() {
		return _evolveDuration;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_offspringSelectionDuration)
			.and(_survivorsSelectionDuration)
			.and(_offspringAlterDuration)
			.and(_offspringFilterDuration)
			.and(_survivorFilterDuration)
			.and(_evaluationDuration)
			.and(_evolveDuration).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(d ->
			eq(_offspringSelectionDuration, d._offspringSelectionDuration) &&
			eq(_survivorsSelectionDuration, d._survivorsSelectionDuration) &&
			eq(_offspringAlterDuration, d._offspringAlterDuration) &&
			eq(_offspringFilterDuration, d._offspringFilterDuration) &&
			eq(_survivorFilterDuration, d._survivorFilterDuration) &&
			eq(_evaluationDuration, d._evaluationDuration) &&
			eq(_evolveDuration, d._evolveDuration)
		);
	}

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
