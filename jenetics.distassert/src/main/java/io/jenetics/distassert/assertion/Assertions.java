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
package io.jenetics.distassert.assertion;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import io.jenetics.distassert.Interval;
import io.jenetics.distassert.assertion.HypothesisTester.Accept;
import io.jenetics.distassert.assertion.HypothesisTester.Reject;
import io.jenetics.distassert.distribution.Distribution;
import io.jenetics.distassert.distribution.NormalDistribution;
import io.jenetics.distassert.distribution.UniformDistribution;
import io.jenetics.distassert.observation.Histogram;
import io.jenetics.distassert.observation.Observation;

/**
 * Entry point for statistical assertion methods.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Assertions {
	private Assertions() {
	}

	/* *************************************************************************
	 * Observation asserts.
	 * ************************************************************************/

	/**
	 * Assertion class for histogram testing.
	 */
	public static class HistogramAssert {
		private final Histogram histogram;

		private Interval range = new Interval(NEGATIVE_INFINITY, POSITIVE_INFINITY);
		private HypothesisTester tester = PearsonsChiSquared.P0_05;
		private Consumer<String> logger = message -> {};

		private HistogramAssert(final Histogram histogram) {
			this.histogram = requireNonNull(histogram);
		}

		/**
		 * Using the given {@code logger} for logging the hypothesis check
		 * information.
		 *
		 * @param logger the check result logger
		 * @return {@code this} assertion object
		 */
		public HistogramAssert usingLogger(final Consumer<String> logger) {
			this.logger = requireNonNull(logger);
			return this;
		}

		/**
		 * Use the given hypothesis tester for checking the observation.
		 *
		 * @param tester the hypothesis tester to use
		 * @return {@code this} assertion object
		 */
		public HistogramAssert usingHypothesisTester(HypothesisTester tester) {
			this.tester = requireNonNull(tester);
			return this;
		}

		/**
		 * Set the range for the hypothesis test.
		 *
		 * @param range the distribution range to test
		 * @return {@code this} assertion object
		 */
		public HistogramAssert withinRange(Interval range) {
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
		public HistogramAssert withinRange(final double min, final double max) {
			return withinRange(new Interval(min, max));
		}

		/**
		 * Checks if the observation follows the given {@code hypothesis}.
		 *
		 * @param hypothesis the expected distribution
		 */
		public void follows(final Distribution hypothesis) {
			final var distribution = new RangedDistribution(hypothesis, range);
			switch (tester.test(histogram, distribution)) {
				case Reject r -> throw new AssertionError(r.message());
				case Accept a -> logger.accept(a.message());
			}
		}

		/**
		 * Checks if the observation is uniformly distributed.
		 */
		public void isUniform() {
			follows(new UniformDistribution(histogram.interval()));
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

	}

	/**
	 * Assertion class for statistical distribution testing.
	 */
	public static final class ObservationAssert extends HistogramAssert {
		private final Observation observation;

		private ObservationAssert(final Observation observation) {
			super(observation.histogram());
			this.observation = requireNonNull(observation);
		}

		@Override
		public ObservationAssert usingLogger(final Consumer<String> logger) {
			super.usingLogger(logger);
			return this;
		}

		@Override
		public ObservationAssert usingHypothesisTester(HypothesisTester tester) {
			super.usingHypothesisTester(tester);
			return this;
		}

		@Override
		public ObservationAssert withinRange(Interval range) {
			super.withinRange(range);
			return this;
		}

		@Override
		public ObservationAssert withinRange(final double min, final double max) {
			return withinRange(new Interval(min, max));
		}

		public void isNormal() {
			final var statistics = observation.statistics();
			follows(new NormalDistribution(
				statistics.mean(),
				Math.sqrt(statistics.variance())
			));
		}

	}

	/**
	 * Return a new distribution assertion object for the given observation.
	 *
	 * @param observation the observation to check.
	 * @return a new distribution assertion object
	 */
	public static HistogramAssert assertThat(Histogram observation) {
		return new HistogramAssert(observation);
	}

	/**
	 * Return a new distribution assertion object for the given observation.
	 *
	 * @param observation the observation to check.
	 * @return a new distribution assertion object
	 */
	public static ObservationAssert assertThat(Observation observation) {
		return new ObservationAssert(observation);
	}

}
