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

import java.util.random.RandomGenerator;

import io.jenetics.util.DoubleRange;
import io.jenetics.util.IntRange;

/**
 * Interface for creating random samples, within the range {@code [0, 1)}, with
 * a given <em>distribution</em>. This interface isn't responsible for creating
 * the random numbers itself. It uses a {@link RandomGenerator} generator, which
 * is given by the caller.
 * {@snippet lang = java:
 * final var random = RandomGenerator.getDefault();
 * final var distribution = Distribution.linear(0.1);
 * // Create a new sample point, which obeys the given distribution.
 * // The random generator is responsible for the base randomness.
 * final double value = distribution.sample(random);
 *}
 *
 * @apiNote
 * The {@link #sample(RandomGenerator)} must return a value with the range
 * {@code [0, 1)}.
 *
 * @see Distributions
 * @see <a href="https://en.wikipedia.org/wiki/Inverse_transform_sampling">Inverse transform sampling</a>
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
	 * Create a new sample point in the range {@code [0, 1)}, which obeys the
	 * defined <em>distribution</em>. For creating such a sample, the given
	 * {@code random} generator is used.
	 *
	 * @param random the random generator used for creating a sample point of
	 *        the defined distribution
	 * @return a new sample point between {@code [0, 1)}
	 */
	double sample(RandomGenerator random);

	/**
	 * Create a new {@code double} sample point within the given range
	 * {@code [min, max)}.
	 *
	 * @param random the random generator used for creating a sample point of
	 *        the defined distribution
	 * @param range the range of the sample point: {@code [min, max)}
	 * @return a new sample point between {@code [min, max)}
	 */
	default double sample(RandomGenerator random, DoubleRange range) {
		final var sample = sample(random);
		assert Double.isFinite(sample);
		assert sample >= 0.0;
		assert sample < 1.0;

		return (sample*(range.max() - range.min())) + range.min();
	}

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

		return (int)((sample*(range.max() - range.min())) + range.min());
	}

}
