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
package org.jenetics;

import static java.lang.String.format;
import static org.jenetics.internal.util.object.eq;

import java.time.Duration;
import java.util.Objects;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-06-30 $</em>
 */
public final class TimeStatistics {

	private final Duration _execution;
	private final Duration _selection;
	private final Duration _alter;
	private final Duration _combine;
	private final Duration _evaluation;
	private final Duration _statistics;

	private TimeStatistics(
		final Duration execution,
		final Duration selection,
		final Duration alter,
		final Duration combine,
		final Duration evaluation,
		final Duration statistics
	) {
		_execution = Objects.requireNonNull(execution);
		_selection = Objects.requireNonNull(selection);
		_alter = Objects.requireNonNull(alter);
		_combine = Objects.requireNonNull(combine);
		_evaluation = Objects.requireNonNull(evaluation);
		_statistics = Objects.requireNonNull(statistics);
	}

	public static TimeStatistics of(
		final Duration execution,
		final Duration selection,
		final Duration alter,
		final Duration combine,
		final Duration evaluation,
		final Duration statistics
	) {
		return new TimeStatistics(
			execution,
			selection,
			alter,
			combine,
			evaluation,
			statistics
		);
	}

	public Duration getExecution() {
		return _execution;
	}

	public Duration getSelection() {
		return _selection;
	}

	public Duration getAlter() {
		return _alter;
	}

	public Duration getCombine() {
		return _combine;
	}

	public Duration getEvaluation() {
		return _evaluation;
	}

	public Duration getStatistics() {
		return _statistics;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_alter)
			.and(_combine)
			.and(_evaluation)
			.and(_execution)
			.and(_selection)
			.and(_statistics).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(time ->
			eq(_alter, time._alter) &&
			eq(_combine, time._combine) &&
			eq(_evaluation, time._evaluation) &&
			eq(_execution, time._execution) &&
			eq(_selection, time._selection) &&
			eq(_statistics, time._statistics)
		);
	}

	@Override
	public String toString() {
		final String pattern = "| %28s: %-26.11f|\n";

		final StringBuilder out = new StringBuilder();
		out.append("+---------------------------------------------------------+\n");
		out.append("|  Time Statistics                                        |\n");
		out.append("+---------------------------------------------------------+\n");
		out.append(format(pattern, "Select time", toSeconds(_selection)));
		out.append(format(pattern, "Alter time", toSeconds(_alter)));
		out.append(format(pattern, "Combine time", toSeconds(_combine)));
		out.append(format(pattern, "Fitness calculation time", toSeconds(_evaluation)));
		out.append(format(pattern, "Statistics calculation time", toSeconds(_statistics)));
		out.append(format(pattern, "Overall execution time", toSeconds(_execution)));
		out.append("+---------------------------------------------------------+");

		return out.toString();
	}

	private static double toSeconds(final Duration duration) {
		return duration.toNanos()/1_000_000_000.0;
	}
}
