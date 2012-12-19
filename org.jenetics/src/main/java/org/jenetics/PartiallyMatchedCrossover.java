/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.jenetics.util.object.hashCodeOf;

import java.util.Random;

import javolution.lang.Immutable;

import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Seq;

/**
 * <p>
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
 * </p>
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
 * to the exchange of the values 3 -> 6, 4 -> 5 and 5 -> 4. To repair the two
 * chromosomes we have to apply this exchange outside the crossing region.
 * <pre>
 *     C1 = 012|654|3789
 *     C2 = 987|345|6210
 * </pre>
 *
 * @see PermutationChromosome
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public final class PartiallyMatchedCrossover<T>
	extends Crossover<EnumGene<T>>
	implements Immutable
{

	public PartiallyMatchedCrossover(final double probability) {
		super(probability);
	}

	@Override
	protected int crossover(
		final MSeq<EnumGene<T>> that,
		final MSeq<EnumGene<T>> other
	) {
		final Random random = RandomRegistry.getRandom();
		int begin = random.nextInt(that.length());
		int end = random.nextInt(other.length());
		begin = min(begin, end);
		end = max(begin, end) + 1;

		swap(that, other, begin, end);
		repair(that, other, begin, end);
		repair(other, that, begin, end);

		return 1;
	}

	private static <T> void swap(
		final MSeq<T> that, final MSeq<T> other,
		final int begin, final int end
	) {
		for (int i = begin; i < end; ++i) {
			final T temp = that.get(i);
			that.set(i, other.get(i));
			other.set(i, temp);
		}
	}

	private static <T> void repair(
		final MSeq<T> that, final MSeq<T> other,
		final int begin, final int end
	) {
		for (int i = 0; i < begin; ++i) {
			int index = indexOf(that, begin, end, that.get(i));
			while (index != -1) {
				that.set(i, other.get(index));
				index = indexOf(that, begin, end, that.get(i));
			}
		}
		for (int i = end, n = that.length(); i < n; ++i) {
			int index = indexOf(that, begin, end, that.get(i));
			while (index != -1) {
				that.set(i, other.get(index));
				index = indexOf(that, begin, end, that.get(i));
			}
		}
	}

	private static int indexOf(
		final Seq<?> genes,
		final int begin, final int end,
		final Object gene
	) {
		int index = -1;
		for (int i = begin; i < end && index == -1; ++i) {
			if (genes.get(i).equals(gene)) {
				index = i;
			}
		}
		return index;
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		return super.equals(obj);
	}

	@Override
	public String toString() {
		return String.format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}

}



