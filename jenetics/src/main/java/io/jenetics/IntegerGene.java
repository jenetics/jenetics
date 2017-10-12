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

import static io.jenetics.internal.math.random.nextInt;
import static io.jenetics.util.RandomRegistry.getRandom;

import java.io.Serializable;
import java.util.Random;

import io.jenetics.internal.util.require;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;
import io.jenetics.util.Mean;

/**
 * NumericGene implementation which holds a 32 bit integer number.
 *
 * <p>This is a <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html">
 * value-based</a> class; use of identity-sensitive operations (including
 * reference equality ({@code ==}), identity hash code, or synchronization) on
 * instances of {@code IntegerGene} may have unpredictable results and should
 * be avoided.
 *
 * @see IntegerChromosome
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 2.0
 * @version 4.0
 */
public final class IntegerGene
	extends AbstractNumericGene<Integer, IntegerGene>
	implements
		NumericGene<Integer, IntegerGene>,
		Mean<IntegerGene>,
		Comparable<IntegerGene>,
		Serializable
{

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new random {@code IntegerGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link IntegerGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	IntegerGene(final Integer value, final Integer min, final Integer max) {
		super(value, min, max);
	}

	@Override
	public int compareTo(final IntegerGene other) {
		return _value.compareTo(other._value);
	}

	/**
	 * Create a new random {@code IntegerGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link IntegerGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @return a new {@code IntegerGene} with the given {@code value}
	 */
	public static IntegerGene of(final int value, final int min, final int max) {
		return new IntegerGene(value, min, max);
	}

	/**
	 * Create a new random {@code IntegerGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link IntegerGene#isValid()} returns {@code false}.
	 *
	 * @since 3.2
	 *
	 * @param value the value of the gene.
	 * @param range the integer range to use
	 * @return a new {@code IntegerGene} with the give {@code value}
	 * @throws NullPointerException if the given {@code range} is {@code null}.
	 */
	public static IntegerGene of(final int value, final IntRange range) {
		return new IntegerGene(value, range.getMin(), range.getMax());
	}

	/**
	 * Create a new random {@code IntegerGene}. It is guaranteed that the value of
	 * the {@code IntegerGene} lies in the interval [min, max].
	 *
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @return a new random {@code IntegerGene}
	 */
	public static IntegerGene of(final int min, final int max) {
		return of(nextInt(getRandom(), min, max), min, max);
	}

	/**
	 * Create a new random {@code IntegerGene}. It is guaranteed that the value of
	 * the {@code IntegerGene} lies in the interval [min, max].
	 *
	 * @since 3.2
	 *
	 * @param range the integer range to use
	 * @return a new random {@code IntegerGene}
	 * @throws NullPointerException if the given {@code range} is {@code null}.
	 */
	public static IntegerGene of(final IntRange range) {
		return of(nextInt(getRandom(), range.getMin(), range.getMax()), range);
	}

	static ISeq<IntegerGene> seq(
		final Integer minimum,
		final Integer maximum,
		final int length
	) {
		require.positive(length);

		final int min = minimum;
		final int max = maximum;
		final Random r = getRandom();

		return MSeq.<IntegerGene>ofLength(length)
			.fill(() -> new IntegerGene(nextInt(r, min, max), minimum, maximum))
			.toISeq();
	}

	@Override
	public IntegerGene newInstance(final Number number) {
		return new IntegerGene(number.intValue(), _min, _max);
	}

	@Override
	public IntegerGene newInstance() {
		return new IntegerGene(
			nextInt(getRandom(), _min, _max), _min, _max
		);
	}

	@Override
	public IntegerGene mean(final IntegerGene that) {
		return new IntegerGene(_value + (that._value - _value)/2, _min, _max);
	}

}
