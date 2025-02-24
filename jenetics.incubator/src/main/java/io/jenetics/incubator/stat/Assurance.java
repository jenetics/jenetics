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
package io.jenetics.incubator.stat;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.function.Consumer;

import org.apache.commons.math4.legacy.stat.inference.ChiSquareTest;

import io.jenetics.incubator.stat.HypothesisTester.Accept;
import io.jenetics.incubator.stat.HypothesisTester.Reject;

/**
 * Entry point for statistical assertion methods.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Assurance {
	private Assurance() {
	}

	/* *************************************************************************
	 * Observation asserts.
	 * ************************************************************************/

	/**
	 * Assertion class for statistical distribution testing.
	 */
	public static final class ObservationAssert {
		private final Observation observation;

		private Interval range = new Interval(NEGATIVE_INFINITY, POSITIVE_INFINITY);
		private HypothesisTester tester = PearsonsChiSquared.P0_05;
		private Consumer<String> logger = message -> {};

		private ObservationAssert(final Observation observation) {
			this.observation = requireNonNull(observation);
		}

		/**
		 * Using the given {@code logger} for logging the hypothesis check
		 * information.
		 *
		 * @param logger the check result logger
		 * @return {@code this} assertion object
		 */
		public ObservationAssert usingLogger(final Consumer<String> logger) {
			this.logger = requireNonNull(logger);
			return this;
		}

		/**
		 * Use the given hypothesis tester for checking the observation.
		 *
		 * @param tester the hypothesis tester to use
		 * @return {@code this} assertion object
		 */
		public ObservationAssert usingHypothesisTester(HypothesisTester tester) {
			this.tester = requireNonNull(tester);
			return this;
		}

		/**
		 * Set the range for the hypothesis test.
		 *
		 * @param range the distribution range to test
		 * @return {@code this} assertion object
		 */
		public ObservationAssert withinRange(Interval range) {
			this.range = requireNonNull(range);
			return this;
		}

		/**
		 * Set the range for the hypothesis test.
		 *
		 * @param min the minimal value of the distribution range
		 * @param max the maximal value of the distribution range
		 * @return {@code this} assertion object
		 */
		public ObservationAssert withinRange(final double min, final double max) {
			return withinRange(new Interval(min, max));
		}

		/**
		 * Checks if the observation follows the given {@code hypothesis}.
		 *
		 * @param hypothesis the expected distribution
		 */
		public void follows(final Distribution hypothesis) {
			final var distribution = new RangedDistribution(hypothesis, range);

			switch (tester.test(observation.histogram(), distribution)) {
				case Reject r -> throw new AssertionError(r.message());
				case Accept a -> logger.accept(a.message());
			}
		}

		/**
		 * Checks if the observation is uniformly distributed.
		 */
		public void isUniform() {
			follows(new UniformDistribution(observation.histogram().interval()));
		}

		/**
		 * Checks if the observation follows a normal distribution with the
		 * given {@code mean} and standard deviation.
		 *
		 * @param mean the mean value of the expected normal distribution
		 * @param stddev the standard deviation of the expected normal
		 *        distribution
		 */
		public void isNormal(double mean, double stddev) {
			follows(new NormalDistribution(mean, stddev));
		}

		/**
		 * Checks if the observation is like the {@code expected} values.
		 *
		 * @param expected the expected values
		 */
		public void isLike(double[] expected) {
			final double[] exp = Arrays.stream(expected)
				.map(v -> Math.max(v, -Double.MAX_VALUE))
				.toArray();
			final long[] hist = observation.histogram().buckets().stream()
				.mapToLong(Histogram.Bucket::count)
				.toArray();

			final var maxChi2 = PearsonsChiSquared.P0_01
				.maxChi2(hist.length - 1);
			final var chi2 = new ChiSquareTest()
				.chiSquare(exp, hist);

			if (chi2 > maxChi2) {
				throw new AssertionError(
					"Data doesn't follow the expected distribution: [max-chi2=%f, chi2=%f]."
						.formatted(maxChi2, chi2)
				);
			}
		}

	}

	/**
	 * Return a new distribution assertion object for the given observation.
	 *
	 * @param observation the observation to check.
	 * @return a new distribution assertion object
	 */
	public static ObservationAssert
	assertThatObservation(Observation observation) {
		return new ObservationAssert(observation);
	}

	/**
	 * Return a new distribution assertion object for the given observation.
	 *
	 * @param observation the observation to check.
	 * @return a new distribution assertion object
	 */
	public static ObservationAssert assertThatObservation(Histogram observation) {
		return new ObservationAssert(Observation.of(observation, Statistics.EMPTY));
	}

}
