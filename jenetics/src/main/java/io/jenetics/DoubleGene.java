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
package io.jenetics;

import static io.jenetics.internal.math.random.nextDouble;
import static io.jenetics.util.RandomRegistry.getRandom;

import java.io.Serializable;
import java.util.Random;

import io.jenetics.internal.util.require;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.Mean;

/**
 * Implementation of the NumericGene which holds a 64 bit floating point number.
 *
 * <p>This is a <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html">
 * value-based</a> class; use of identity-sensitive operations (including
 * reference equality ({@code ==}), identity hash code, or synchronization) on
 * instances of {@code DoubleGene} may have unpredictable results and should
 * be avoided.
 *
 * @see DoubleChromosome
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 4.0
 */
public final class DoubleGene
	extends AbstractNumericGene<Double, DoubleGene>
	implements
		NumericGene<Double, DoubleGene>,
		Mean<DoubleGene>,
		Comparable<DoubleGene>,
		Serializable
{

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new random {@code DoubleGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max),
	 * no exception is thrown. In this case the method
	 * {@link DoubleGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (exclusively).
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	DoubleGene(final Double value, final Double min, final Double max) {
		super(value, min, max);
	}

	@Override
	public int compareTo(final DoubleGene other) {
		return _value.compareTo(other._value);
	}

	/**
	 * Create a new random {@code DoubleGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max),
	 * no exception is thrown. In this case the method
	 * {@link DoubleGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (exclusively).
	 * @return a new {@code DoubleGene} with the given parameter
	 */
	public static DoubleGene of(
		final double value,
		final double min,
		final double max
	) {
		return new DoubleGene(value, min, max);
	}

	/**
	 * Create a new random {@code DoubleGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max),
	 * no exception is thrown. In this case the method
	 * {@link DoubleGene#isValid()} returns {@code false}.
	 *
	 * @since 3.2
	 *
	 * @param value the value of the gene.
	 * @param range the double range to use
	 * @return a new random {@code DoubleGene}
	 * @throws NullPointerException if the given {@code range} is {@code null}.
	 */
	public static DoubleGene of(final double value, final DoubleRange range) {
		return new DoubleGene(value, range.getMin(), range.getMax());
	}

	/**
	 * Create a new random {@code DoubleGene}. It is guaranteed that the value
	 * of the {@code DoubleGene} lies in the interval [min, max).
	 *
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (exclusively).
	 * @return a new {@code DoubleGene} with the given parameter
	 */
	public static DoubleGene of(final double min, final double max) {
		return of(nextDouble(getRandom(), min, max), min, max);
	}

	/**
	 * Create a new random {@code DoubleGene}. It is guaranteed that the value
	 * of the {@code DoubleGene} lies in the interval [min, max).
	 *
	 * @since 3.2
	 *
	 * @param range the double range to use
	 * @return a new {@code DoubleGene} with the given parameter
	 * @throws NullPointerException if the given {@code range} is {@code null}.
	 */
	public static DoubleGene of(final DoubleRange range) {
		return of(nextDouble(getRandom(), range.getMin(), range.getMax()), range);
	}

	static ISeq<DoubleGene> seq(
		final Double minimum,
		final Double maximum,
		final int length
	) {
		require.positive(length);

		final double min = minimum;
		final double max = maximum;
		final Random r = getRandom();

		return MSeq.<DoubleGene>ofLength(length)
			.fill(() -> new DoubleGene(nextDouble(r, min, max), minimum, maximum))
			.toISeq();
	}

	@Override
	public DoubleGene newInstance(final Number number) {
		return new DoubleGene(number.doubleValue(), _min, _max);
	}

	@Override
	public DoubleGene newInstance() {
		return new DoubleGene(
			nextDouble(getRandom(), _min, _max), _min, _max
		);
	}

	@Override
	public DoubleGene mean(final DoubleGene that) {
		return new DoubleGene(_value + (that._value - _value)/2.0, _min, _max);
	}

}
