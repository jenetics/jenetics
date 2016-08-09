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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics;

import static java.lang.String.format;

import java.util.Random;

import org.jenetics.internal.math.base;
import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;

/**
 * The {@code PartiallyMatchedCrossover} (PMX) guarantees that all {@link Gene}s
 * are found exactly once in each chromosome. No gene is duplicated by this
 * crossover. The PMX can be applied usefully in the TSP or other permutation
 * problem encodings. Permutation encoding is useful for all problems where the
 * fitness only depends on the ordering of the genes within the chromosome. This
 * is the case in many combinatorial optimization problems. Other crossover
 * operators for combinatorial optimization are:
 * <ul type="square">
 *     <li>order crossover</li>
 *     <li>cycle crossover</li>
 *     <li>edge recombination crossover</li>
 *     <li>edge assembly crossover</li>
 * </ul>
 * <p>
 * The PMX is similar to the two-point crossover. A crossing region is chosen
 * by selecting two crossing points.
 * <pre>
 *     C1 = 012|345|6789
 *     C2 = 987|654|3210
 * </pre>
 * After performing the crossover we normally got two invalid chromosomes.
 * <pre>
 *     C1 = 012|654|6789
 *     C2 = 987|345|3210
 * </pre>
 * Chromosome {@code C1} contains the value 6  twice and misses the value
 * 3. On  the other side chromosome {@code C2} contains the value 3 twice and
 * misses the value 6. We can observe that this crossover is equivalent
 * to the exchange of the values {@code 3 -> 6}, {@code 4 -> 5} and
 * {@code 5 -> 4}. To repair the two
 * chromosomes we have to apply this exchange outside the crossing region.
 * <pre>
 *     C1 = 012|654|3789
 *     C2 = 987|345|6210
 * </pre>
 *
 * <em>The {@code PartiallyMatchedCrossover} class requires chromosomes with the
 * same length. An {@code IllegalArgumentException} is thrown at runtime if this
 * requirement is not fulfilled.</em>
 *
 * @see PermutationChromosome
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.6
 */
public final class PartiallyMatchedCrossover<T, C extends Comparable<? super C>>
	extends Crossover<EnumGene<T>, C>
{

	public PartiallyMatchedCrossover(final double probability) {
		super(probability);
	}

	@Override
	protected int crossover(
		final MSeq<EnumGene<T>> that,
		final MSeq<EnumGene<T>> other
	) {
		if (that.length() != other.length()) {
			throw new IllegalArgumentException(format(
				"Required chromosomes with same length: %s != %s",
				that.length(), other.length()
			));
		}

		if (that.length() >= 2) {
			final Random random = RandomRegistry.getRandom();
			final int[] points = base.subset(that.length(), 2, random);

			that.swap(points[0], points[1], other, points[0]);
			repair(that, other, points[0], points[1]);
			repair(other, that, points[0], points[1]);
		}

		return 1;
	}

	private static <T> void repair(
		final MSeq<T> that, final MSeq<T> other,
		final int begin, final int end
	) {
		for (int i = 0; i < begin; ++i) {
			int index = that.indexOf(that.get(i), begin, end);
			while (index != -1) {
				that.set(i, other.get(index));
				index = that.indexOf(that.get(i), begin, end);
			}
		}
		for (int i = end, n = that.length(); i < n; ++i) {
			int index = that.indexOf(that.get(i), begin, end);
			while (index != -1) {
				that.set(i, other.get(index));
				index = that.indexOf(that.get(i), begin, end);
			}
		}
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(super::equals);
	}

	@Override
	public String toString() {
		return format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}

}
