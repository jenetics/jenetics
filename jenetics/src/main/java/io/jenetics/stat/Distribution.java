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
package io.jenetics.stat;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.longBitsToDouble;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.random.RandomGenerator;

import io.jenetics.util.IntRange;

/**
 * Interface for creating random samples, within the range {@code [0, 1)}, with
 * a given <em>distribution</em>.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
@FunctionalInterface
public interface Distribution {

	/**
	 * Default uniform distribution by calling
	 * {@link RandomGenerator#nextDouble()}.
	 */
	Distribution UNIFORM = RandomGenerator::nextDouble;

	/**
	 * Default gaussian distribution by calling
	 * {@link RandomGenerator#nextGaussian()}.
	 */
	Distribution GAUSSIAN = RandomGenerator::nextGaussian;

	/**
	 * Create a new sample, within the range {@code [0, 1)}, which obeys the
	 * defined <em>distribution</em>. For creating such a sample, the given
	 * {@code random} generator is used.
	 *
	 * @param random the random generator used for creating a sample point of
	 *        the defined distribution
	 * @return a new sample point between {@code [0, 1)}
	 */
	double sample(RandomGenerator random);

	/**
	 * Create a new {@code int} sample point within the given range
	 * {@code [min, max)}.
	 *
	 * @param random the random generator used for creating a sample point of
	 *        the defined distribution
	 * @param range the range of the sample point: {@code [min, max)}
	 * @return a new sample point between {@code [min, max)}
	 */
	default int sample(RandomGenerator random, IntRange range) {
		final var sample = sample(random);
		assert Double.isFinite(sample);
		assert sample >= 0.0;
		assert sample < 1.0;

		return (int)((sample(random)*(range.max() - range.min())) + range.min());
	}

	static Distribution linear(final double mean) {
		if (mean < 0 || mean >= 1) {
			throw new IllegalArgumentException(
				"Mean value not within allowed range [0, 1): %f."
					.formatted(mean)
			);
		}

		final class Range {
			static final double MIN = 0.0;
			static final double MAX = longBitsToDouble(doubleToLongBits(1) - 1);
			static double clamp(final double value) {
				return Math.clamp(value, MIN, MAX);
			}
		}

		if (Double.compare(mean, 0) == 0) {
			return random -> Range.MIN;
		} else if (mean == Range.MAX) {
			return random -> Range.MAX;
		}

		final double b, m;
		if (mean < 0.292893) {
			b = (2 - sqrt(2))/mean;
			m = -pow(b, 2)/2;
		} else if (mean < 0.707107) {
			b = (pow(mean, 2) - 0.5)/(pow(mean, 2) - mean);
			m = 2 - 2*b;
		} else if (mean < 1) {
			b = 0.5*(1 - pow(((2 - sqrt(2))/(1 - mean) - 1), 2));
			m = -b + sqrt(1 - 2*b) + 1;
		} else {
			b = m = 1;
		}

		return random -> {
			final var r = random.nextDouble();

			if (mean < 0.5) {
				return Range.clamp((-b + sqrt(pow(b, 2) + 2*r*m))/m);
			} else if (mean == 0.5) {
				return r;
			} else {
				return Range.clamp((b - sqrt(pow(b, 2) + 2*m*(r - 1 + b + 0.5*m)))/-m);
			}
		};
	}

}
