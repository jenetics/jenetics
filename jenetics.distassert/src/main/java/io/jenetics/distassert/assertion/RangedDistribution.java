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

import static java.lang.Math.clamp;
import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

import org.apache.commons.math4.legacy.analysis.integration.SimpsonIntegrator;

import io.jenetics.distassert.distribution.Cdf;
import io.jenetics.distassert.distribution.Distribution;
import io.jenetics.distassert.distribution.Pdf;
import io.jenetics.distassert.observation.Interval;

/**
 * Takes a given distribution and restricts its range. The functions {@link #pdf()}}
 * and {@link #cdf()} are adapted to preserve the statistical properties of the
 * restricted distribution:
 * <ul>
 * <li>PDF: </li>The area under the PDF is 1.
 * <li>CDF: </li> cdf(0) = 0 and cdf(infinity) = 1.
 * </ul>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class RangedDistribution implements Distribution {

	private static final int MAX_EVALUATIONS = 1_000_000;

	private final Distribution distribution;
	private final Interval range;

	private final Pdf distPdf;
	private final double distPdfScale;

	private RangedDistribution(
		final Distribution distribution,
		final Interval range
	) {
		this.distribution = requireNonNull(distribution);
		this.range = requireNonNull(range);

		distPdf = distribution.pdf();
		distPdfScale = 1.0/integrate(distPdf::apply, range.min(), range.max());
	}

	private double integrate(
		final DoubleUnaryOperator fn,
		final double min,
		final double max
	) {
		return new SimpsonIntegrator().integrate(
			MAX_EVALUATIONS,
			fn::applyAsDouble,
			min, max
		);
	}

	@Override
	public Pdf pdf() {
		if (NumericalContext.CONTEXT.isOne(distPdfScale)) {
			return distribution.pdf();
		}

		return x -> {
			if (x < range.min() || x > range().max()) {
				return 0;
			}
			return distPdf.apply(x)*distPdfScale;
		};
	}

	@Override
	public Cdf cdf() {
		if (NumericalContext.CONTEXT.isOne(distPdfScale)) {
			return distribution.cdf();
		}

		final var pdf = pdf();
		return x -> {
			if (x <= range.min()) {
				return 0;
			}
			if (x >= range.max()) {
				return 1;
			}

			return clamp(integrate(pdf::apply, range.min(), x), 0, 1);
		};
	}

	/**
	 * Return the underlying, restricted distribution.
	 *
	 * @return the underlying, restricted distribution.
	 */
	public Distribution distribution() {
		return distribution;
	}

	/**
	 * Return the range of the restricted distribution.
	 *
	 * @return the range of the restricted distribution
	 */
	public Interval range() {
		return range;
	}

	@Override
	public int hashCode() {
		return Objects.hash(distribution, range);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof RangedDistribution dist &&
			Objects.equals(distribution, dist.distribution) &&
			Objects.equals(range, dist.range);
	}

	@Override
	public String toString() {
		return "RangedDistribution[distribution=%s, range=%s]"
			.formatted(distribution, range);
	}

	/**
	 * Return a distribution, restricted to the given {@code range}. The
	 * 
	 *
	 * @param distribution the distribution, where only a range is valid
	 * @param range the range of the distribution
	 * @return a distribution, restricted to the given {@code range}
	 */
	static Distribution of(final Distribution distribution, final Interval range) {
		if (distribution.domain().equals(range)) {
			return distribution;
		}

		if (range.contains(distribution.domain())) {
			return distribution;
		}

		if (distribution instanceof RangedDistribution rd) {
			final var rng = rd.range.intersect(range).orElseThrow(() ->
				new IllegalArgumentException("Combined range is empty.")
			);
			return of(rd.distribution, rng);
		} else {
			return new RangedDistribution(distribution, range);
		}
	}

}
