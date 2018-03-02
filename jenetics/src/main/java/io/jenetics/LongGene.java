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

import static java.lang.String.format;
import static io.jenetics.util.RandomRegistry.getRandom;

import java.io.Serializable;
import java.util.Random;

import io.jenetics.internal.math.random;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.LongRange;
import io.jenetics.util.MSeq;
import io.jenetics.util.Mean;

/**
 * NumericGene implementation which holds a 64 bit integer number.
 *
 * <p>This is a <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html">
 * value-based</a> class; use of identity-sensitive operations (including
 * reference equality ({@code ==}), identity hash code, or synchronization) on
 * instances of {@code LongGene} may have unpredictable results and should
 * be avoided.
 *
 * @see LongChromosome
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 4.0
 */
public final class LongGene
	extends AbstractNumericGene<Long, LongGene>
	implements
		NumericGene<Long, LongGene>,
		Mean<LongGene>,
		Comparable<LongGene>,
		Serializable
{

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new random {@code LongGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link LongGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	LongGene(final Long value, final Long min, final Long max) {
		super(value, min, max);
	}

	@Override
	public int compareTo(final LongGene other) {
		return _value.compareTo(other._value);
	}

	/**
	 * Create a new random {@code LongGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link LongGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @return a new {@code LongGene} with the given parameters.
	 */
	public static LongGene of(final long value, final long min, final long max) {
		return new LongGene(value, min, max);
	}

	/**
	 * Create a new random {@code LongGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link LongGene#isValid()} returns {@code false}.
	 *
	 * @since 3.2
	 *
	 * @param value the value of the gene.
	 * @param range the long range to use
	 * @return a new random {@code LongGene}
	 * @throws NullPointerException if the given {@code range} is {@code null}.
	 */
	public static LongGene of(final long value, final LongRange range) {
		return new LongGene(value, range.getMin(), range.getMax());
	}

	/**
	 * Create a new random {@code LongGene}. It is guaranteed that the value of
	 * the {@code LongGene} lies in the interval [min, max].
	 *
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @return a new {@code LongGene} with the given parameters.
	 */
	public static LongGene of(final long min, final long max) {
		return of(nextLong(getRandom(), min, max), min, max);
	}

	/**
	 * Create a new random {@code LongGene}. It is guaranteed that the value of
	 * the {@code LongGene} lies in the interval [min, max].
	 *
	 * @since 3.2
	 *
	 * @param range the long range to use
	 * @return a new random {@code LongGene}
	 * @throws NullPointerException if the given {@code range} is {@code null}.
	 */
	public static LongGene of(final LongRange range) {
		return of(nextLong(getRandom(), range.getMin(), range.getMax()), range);
	}

	static ISeq<LongGene> seq(
		final Long minimum,
		final Long maximum,
		final IntRange lengthRange
	) {
		final long min = minimum;
		final long max = maximum;
		final Random r = getRandom();

		return MSeq.<LongGene>ofLength(random.nextInt(lengthRange, r))
			.fill(() -> new LongGene(nextLong(r, min, max), minimum, maximum))
			.toISeq();
	}

	@Override
	public LongGene newInstance(final Number number) {
		return new LongGene(number.longValue(), _min, _max);
	}

	@Override
	public LongGene newInstance() {
		return new LongGene(
			nextLong(getRandom(), _min, _max), _min, _max
		);
	}

	@Override
	public LongGene mean(final LongGene that) {
		return new LongGene(_value + (that._value - _value)/2, _min, _max);
	}

	/**
	 * Returns a pseudo-random, uniformly distributed int value between min
	 * and max (min and max included).
	 *
	 * @param random the random engine to use for calculating the random
	 *        long value
	 * @param min lower bound for generated long integer
	 * @param max upper bound for generated long integer
	 * @return a random long integer greater than or equal to {@code min}
	 *         and less than or equal to {@code max}
	 * @throws IllegalArgumentException if {@code min > max}
	 * @throws NullPointerException if the given {@code random}
	 *         engine is {@code null}.
	 */
	static long nextLong(
		final Random random,
		final long min, final long max
	) {
		if (min > max) {
			throw new IllegalArgumentException(format(
				"min >= max: %d >= %d.", min, max
			));
		}

		final long diff = (max - min) + 1;
		long result = 0;

		if (diff <= 0) {
			do {
				result = random.nextLong();
			} while (result < min || result > max);
		} else if (diff < Integer.MAX_VALUE) {
			result = random.nextInt((int)diff) + min;
		} else {
			result = nextLong(random, diff) + min;
		}

		return result;
	}

	/**
	 * Returns a pseudo-random, uniformly distributed int value between 0
	 * (inclusive) and the specified value (exclusive), drawn from the given
	 * random number generator's sequence.
	 *
	 * @param random the random engine used for creating the random number.
	 * @param n the bound on the random number to be returned. Must be
	 *        positive.
	 * @return the next pseudo-random, uniformly distributed int value
	 *         between 0 (inclusive) and n (exclusive) from the given random
	 *         number generator's sequence
	 * @throws IllegalArgumentException if n is smaller than 1.
	 * @throws NullPointerException if the given {@code random}
	 *         engine is {@code null}.
	 */
	static long nextLong(final Random random, final long n) {
		if (n <= 0) {
			throw new IllegalArgumentException(format(
				"n is smaller than one: %d", n
			));
		}

		long bits;
		long result;
		do {
			bits = random.nextLong() & 0x7fffffffffffffffL;
			result = bits%n;
		} while (bits - result + (n - 1) < 0);

		return result;
	}

}
