/*
 * Java Genetic Algorithm Library (@__identifier__@).
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
 */
package io.jenetics;

import io.jenetics.internal.math.Subset;
import io.jenetics.util.BaseSeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.Seq;

/**
 * The {@code UniformOderBasedCrossover} guarantees that all {@link Gene}s
 * are found exactly once in each chromosome. No gene is duplicated by this
 * crossover. This crossover can be applied usefully in the TSP or other
 * permutation problem encodings. Permutation encoding is useful for all problems
 * where the fitness only depends on the ordering of the genes within the
 * chromosome. This is the case in many combinatorial optimization problems.
 * Other crossover operators for combinatorial optimization are:
 * <ul>
 *     <li>order crossover</li>
 *     <li>cycle crossover</li>
 *     <li>edge recombination crossover</li>
 *     <li>edge assembly crossover</li>
 *     <li>partially matched crossover</li>
 * </ul>
 * <p>
 * Within the uniform order-based crossover, a set of positions is chosen
 * randomly. The genes at the positions are reordered in the order they occur in
 * the other parent.
 * <pre>
 *    C1 = 0123456789
 *    C2 = 9876543210
 *    Positions = 2, 4, 5, 7, 8
 * </pre>
 *  The values at the positions are removed
 * <pre>
 *    C1 = 01_3__6__9
 *    C2 = 9__6__3_10
 *    Order of removed values in C1 = 2, 4, 5, 7, 8
 *    Order of removed values in C2 = 8, 7, 5, 4, 2
 * </pre>
 * The removed values are added in the order they occur in the other chromosome
 * <pre>
 *    C1 = 0183756429
 *    C2 = 9246573810
 * </pre>
 *
 * @see <a href="https://doi.org/10.1007/978-3-030-42227-1_12">
 *     Elements of evolutionary algorithms. Computational intelligence:
 *     A methodological introduction (pp. 255–285).</a>
 * @see PermutationChromosome
 *
 * @author <a href="mailto:feichtenschlager10@gmail.com">Paul Feichtenschlager</a>
 * @version 8.0
 * @since 8.0
 */
public class UniformOderBasedCrossover<T, C extends Comparable<? super C>>
	extends Crossover<EnumGene<T>, C>
{

	/**
	 * Create a new UniformOrderBasedCrossover instance
	 *
	 * @param probability the recombination probability as defined in
	 *        {@link Crossover#Crossover(double)}. This is the probability that
	 *        a given individual is selected for crossover.
	 * @throws IllegalArgumentException if the probability is not in the
	 *         valid range of {@code [0, 1]}
	 */
	public UniformOderBasedCrossover(double probability) {
	    super(probability);
	}

	/**
	 * Applies uniform order-based crossover to two sequences. A set of positions
	 * is chosen, the genes at those positions are reordered as they occur in
	 * the other sequence.
	 *
	 * @param that first sequence
	 * @param other second sequence
	 * @throws IllegalArgumentException if the two input sequences have a
	 *         different length
	 */
	@Override
	protected int crossover(
		final MSeq<EnumGene<T>> that,
		final MSeq<EnumGene<T>> other
	) {
		if (that.length() != other.length()) {
			throw new IllegalArgumentException(
				"Required chromosomes with same length: %d != %d"
					.formatted(that.length(), other.length())
			);
		}

		if (that.length() >= 2) {
			final var random = RandomRegistry.random();
			final var positions = Subset.next(
				random, that.length(), that.length()/2
			);

			final var removed1 = selectAt(positions, that);
			final var removed2 = selectAt(positions, other);

			final var reordered1 = reorder(removed1, other);
			final var reordered2 = reorder(removed2, that);

			exchange(positions, reordered1, that);
			exchange(positions, reordered2, other);

			return positions.length;
		} else {
			return 0;
		}
	}

	private static <T> void
	exchange(final int[] indexes, final BaseSeq<T> ordered, final MSeq<T> seq) {
		for (int i = 0; i < indexes.length; ++i) {
			seq.set(indexes[i], ordered.get(i));
		}
	}

	private static <T> Seq<T> reorder(final Seq<T> removed, final Seq<T> seq) {
	    return removed.stream()
			.map(seq::lastIndexOf)
			.sorted(Integer::compareTo)
			.map(seq::get)
			.collect(Seq.toSeq());
	}

	private static <T> Seq<T> selectAt(final int[] indexes, final BaseSeq<T> seq) {
		final var selected = MSeq. <T>ofLength(indexes.length);
		for (int i = 0; i < indexes.length; ++i) {
			selected.set(i, seq.get(indexes[i]));
		}
		return selected;
	}

}
