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
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

	/**
	 * Assertion class for statistical distribution testing.
	 */
	public static final class DistributionAssert {
		private final Supplier<Histogram> observation;

		private Interval range = new Interval(NEGATIVE_INFINITY, POSITIVE_INFINITY);
		private HypothesisTester tester = PearsonsChiSquared.P0_05;
		private Consumer<String> logger = message -> {};

		private DistributionAssert(final Supplier<Histogram> observation) {
			this.observation = requireNonNull(observation);
		}

		/**
		 * Using the given {@code logger} for logging the hypothesis check
		 * information.
		 *
		 * @param logger the check result logger
		 * @return {@code this} assertion object
		 */
		public DistributionAssert withLogger(final Consumer<String> logger) {
			this.logger = requireNonNull(logger);
			return this;
		}

		/**
		 * Use the given hypothesis tester for checking the observation.
		 *
		 * @param tester the hypothesis tester to use
		 * @return {@code this} assertion object
		 */
		public DistributionAssert usingHypothesisTester(HypothesisTester tester) {
			this.tester = requireNonNull(tester);
			return this;
		}

		/**
		 * Set the range for the hypothesis test.
		 *
		 * @param range the distribution range to test
		 * @return {@code this} assertion object
		 */
		public DistributionAssert withinRange(Interval range) {
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
		public DistributionAssert withinRange(final double min, final double max) {
			return withinRange(new Interval(min, max));
		}

		/**
		 * Checks if the observation follows the given {@code hypothesis}.
		 *
		 * @param hypothesis the expected distribution
		 */
		public void follows(final Distribution hypothesis) {
			final var distribution = new RangedDistribution(hypothesis, range);

			switch (tester.test(observation.get(), distribution)) {
				case Reject r -> throw new AssertionError(r.message());
				case Accept a -> logger.accept(a.message());
			}
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
			final long[] hist = observation.get().buckets().stream()
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

		/**
		 * Checks if the observation is uniformly distributed.
		 */
		public void isUniform() {
			final var interval = observation.get()
				.buckets().partition().interval();
			follows(new UniformDistribution(interval));
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

	public static final class SamplesAssert {
		private final Sampling sampling;

		private SamplesAssert(final Sampling sampling) {
			this.sampling = requireNonNull(sampling);
		}

		public DistributionAssert usingPartitioning(final Histogram.Partition partition) {
			final Supplier<Histogram> observation =
				() -> new Histogram.Builder(partition).build(sampling);

			return new DistributionAssert(observation);
		}

		public DistributionAssert usingPartitioning(final Interval interval, final int classes) {
			return usingPartitioning(Histogram.Partition.of(interval, classes));
		}

		public DistributionAssert usingPartitioning(final double min, final double max, final int classes) {
			return usingPartitioning(new Interval(min, max), classes);
		}

	}

	/**
	 * Return a new distribution assertion object for the given observation.
	 *
	 * @param observation the observation to check.
	 * @return a new distribution assertion object
	 */
	public static DistributionAssert assertThatObservation(Histogram observation) {
		return new DistributionAssert(() -> observation);
	}

	public static SamplesAssert assertThatObservation(final Sampling sampling) {
		return new SamplesAssert(sampling);
	}

	public static DistributionAssert assertThat(Observation observation) {
		return null;
	}



}
