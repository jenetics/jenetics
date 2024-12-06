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

import static java.util.Objects.requireNonNull;

import java.util.Arrays;

import org.apache.commons.math4.legacy.stat.inference.ChiSquareTest;

import io.jenetics.incubator.stat.HypothesisTester.Accept;
import io.jenetics.incubator.stat.HypothesisTester.Reject;
import io.jenetics.util.DoubleRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public final class StatisticsAssert {
	private StatisticsAssert() {
	}

	public static final class DistributionAssert {
		private final Histogram _observation;

		private HypothesisTester _tester = PearsonChi2Tester.P_0001;

		private DistributionAssert(final Histogram observation) {
			_observation = requireNonNull(observation);
		}

		public DistributionAssert withTester(HypothesisTester tester) {
			_tester = requireNonNull(tester);
			return this;
		}

		public void follows(final Distribution hypothesis) {
			switch (_tester.test(_observation, hypothesis)) {
				case Accept r -> System.out.println(r.message());
				case Reject r -> throw new AssertionError(r.message());
			}
		}

		public void isLike(double[] expected) {
			final double[] exp = Arrays.stream(expected)
				.map(v -> Math.max(v, -Double.MAX_VALUE))
				.toArray();
			final long[] hist = _observation.buckets().stream()
				.mapToLong(Histogram.Bucket::count)
				.toArray();

			final var maxChi2 = PearsonChi2Tester.P_001
				.maxChi2(hist.length - 1);
			final var chi2 = new ChiSquareTest()
				.chiSquare(exp, hist);

			if (chi2 > maxChi2) {
				throw new AssertionError(
					"Data doesn't follow the expected distribution: [max-chi2=%f, chi2=%f]."
						.formatted(maxChi2, chi2)
				);
			} else {
				System.out.printf(
					"Data follow the expected distribution: [max-chi2=%f, chi2=%f].%n",
					maxChi2, chi2
				);
			}
		}

		public void isUniform() {
			final var range = DoubleRange.of(
				Math.max(_observation.buckets().getFirst().min(), -Double.MAX_VALUE),
				Math.min(_observation.buckets().getLast().max(), Double.MAX_VALUE)
			);
			follows(new UniformDistribution(range));
		}

		public void isNormal(double mean, double stddev) {
			follows(new NormalDistribution(mean, stddev));
		}

		public void isNormal(double mean, double stddev, DoubleRange range) {
			follows(new RangedDistribution(new NormalDistribution(mean, stddev), range));
		}

	}

	public static DistributionAssert assertThatObservation(Histogram observation) {
		return new DistributionAssert(observation.slice(1, -1));
	}

}
