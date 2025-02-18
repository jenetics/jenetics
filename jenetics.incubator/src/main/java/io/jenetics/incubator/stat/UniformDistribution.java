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

import java.util.NoSuchElementException;
import java.util.random.RandomGenerator;

import io.jenetics.stat.Sampler;
import io.jenetics.util.DoubleRange;

/**
 * <a href="http://en.wikipedia.org/wiki/Uniform_distribution_%28continuous%29">
 * Uniform distribution</a> class.
 *
 * @see LinearDistribution
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public record UniformDistribution(Interval domain) implements Distribution {

	/**
	 * Return a sampler class, which creates uniformly distributed values within
	 * the allowed {@link #domain()}. The returned sample may throw a
	 * {@link NoSuchElementException} if the range, the
	 * {@link Sampler#sample(RandomGenerator, DoubleRange)} method is called
	 * with and the distribution's {@link #domain()} have no intersection.
	 *
	 * @return a uniform distribution sample
	 */
	@Override
	public Sampler sampler() {
		return (random, range) -> {
			final var rng = domain.intersect(new Interval(range.min(), range.max()));
			if (rng.isEmpty()) {
				throw new NoSuchElementException("""
					The domain of the distribution and the given range have no \
					intersection: %s ∩ %s == Ø.
					""".formatted(domain, random)
				);
			} else {
				return random.nextDouble(rng.get().min(), rng.get().max());
			}
		};
	}

	/**
	 * Return a new PDF object.
	 *
	 * <p>
	 * <img
	 *     src="doc-files/uniform-pdf.gif"
	 *     alt="f(x)=\left\{\begin{matrix}
	 *          \frac{1}{max-min} & for & x \in [min, max] \\
	 *          0 & & otherwise \\
	 *          \end{matrix}\right."
	 * >
	 * </p>
	 *
	 */
	@Override
	public Pdf pdf() {
		return x ->
			(x >= domain.min() && x <= domain.max())
				? 1.0/(domain.max() - domain.min())
				: 0.0;
	}

	/**
	 * Return a new CDF object.
	 *
	 * <p>
	 * <img
	 *     src="doc-files/uniform-cdf.gif"
	 *     alt="f(x)=\left\{\begin{matrix}
	 *         0 & for & x < min \\
	 *         \frac{x-min}{max-min} & for & x \in [min, max] \\
	 *         1 & for & x > max  \\
	 *         \end{matrix}\right."
	 * >
	 * </p>
	 *
	 */
	@Override
	public Cdf cdf() {
		return x -> {
			final double divisor = domain.max() - domain.min();

			double result = 0.0;
			if (x < domain.min()) {
				result = 0.0;
			} else if (x > domain.max()) {
				result = 1.0;
			} else {
				result = (x - domain.min())/divisor;
			}

			return result;
		};
	}

}
