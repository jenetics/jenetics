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

import static java.util.Objects.requireNonNull;

import java.util.random.RandomGenerator;

import io.jenetics.internal.math.Subsets;
import io.jenetics.stat.Sampler;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;

/**
 * The shuffle mutation, changes the order of the genes between two randomly
 * chosen positions. The genes between the positions are shuffled. This mutation
 * operator can also be used for combinatorial problems, where no duplicated
 * genes within a chromosome are allowed, e.g., for the TSP.
 * <p>
 *	<img src="doc-files/ShuffleMutator.svg" width="450"
 *	     alt="Shuffle mutator" >
 *
 * @see <a href="https://doi.org/10.1007/978-3-030-42227-1_12">
 *     Elements of evolutionary algorithms. Computational intelligence:
 *     A methodological introduction (pp. 255–285).</a>
 * @see Mutator
 *
 * @author <a href="mailto:feichtenschlager10@gmail.com">Paul Feichtenschlager</a>
 * @version 8.0
 * @since 8.0
 */
public class ShuffleMutator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Mutator<G, C>
{

	/**
	 * Represents the chromosome range which will be shuffled
	 *
	 * @param a the start index (inclusively)
	 * @param b the end index (exclusively)
	 */
	public record Range(int a, int b) {
		public Range {
			if (a < 0 || b < 0 || b <= a) {
				throw new IllegalArgumentException(
					"Invalid range: [%d, %d].".formatted(a, b)
				);
			}
		}
	}

	/**
	 * Functional interface for creating random range objects for
	 * shuffling sequences of a given length.
	 */
	@FunctionalInterface
	public interface RangeRandom {

		/**
		 * Random range generator, which creates ranges with uniformly
		 * distributed range indexes. Both the length and the shift indexes
		 * are chosen uniformly.
		 */
		RangeRandom UNIFORM = (random, length) -> {
			if (length <= 1) {
				throw new IllegalArgumentException(
					"Length must be greater then 1, but was %d."
						.formatted(length)
				);
			}

			final int[] points = Subsets.next(random, length + 1, 2);
			return new Range(points[0], points[1]);
		};

		/**
		 * Create a new <em>random</em> range for shuffling sequences with a
		 * given {@code length}.
		 *
		 * @param random the random generator to be used for creating a new
		 *        range
		 * @param length the length of the sequence to be shuffled
		 * @return a new <em>randomly</em> created shuffle range
		 */
		Range newRange(final RandomGenerator random, final int length);

		/**
		 * Create a new random range generator, which uses the given distributions
		 * for creating the range points.
		 *
		 * @param lengthSampler the sampler used for creating the shifted gene
		 *        count
		 * @param indexSampler the sampler used for creating the shift indexes
		 * @return a new random range generator with the given parameters
		 */
		static RangeRandom of(
			final Sampler lengthSampler,
			final Sampler indexSampler
		) {
			requireNonNull(lengthSampler);
			requireNonNull(indexSampler);

			return (random, size) -> {
				if (size <= 1) {
					throw new IllegalArgumentException(
						"Length must be greater then 1, but was %d."
							.formatted(size)
					);
				}

				final int lng = lengthSampler.sample(random, IntRange.of(1, size));
				final int a = indexSampler.sample(random, IntRange.of(0, size - lng));
				final int b = a + lng;

				return new Range(a, b);
			};
		}

		/**
		 * Create a new random range generator, which uses the given distributions
		 * for creating the shift points. The shift indexes are uniformly
		 * distributed.
		 *
		 * @see #of(Sampler, Sampler)
		 *
		 * @param lengthSampler the sampler used for creating the shifted gene
		 *        count
		 * @return a new random shift generator with the given parameters
		 */
		static RangeRandom of(final Sampler lengthSampler) {
			return of(lengthSampler, Sampler.UNIFORM);
		}
	}

	private final RangeRandom _random;

	/**
	 * Constructs an alterer with a given recombination probability and random
	 * range generator.
	 *
	 * @param probability the crossover probability.
	 * @param random the random range generator used by the mutator
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}.
	 */
	public ShuffleMutator(final RangeRandom random, final double probability) {
		super(probability);
		_random = requireNonNull(random);
	}

	/**
	 * Constructs an alterer with a given recombination probability.
	 *
	 * @param probability the crossover probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}.
	 */
	public ShuffleMutator(final double probability) {
		this(RangeRandom.UNIFORM, probability);
	}

	/**
	 * Default constructor, with default mutation probability
	 * ({@link AbstractAlterer#DEFAULT_ALTER_PROBABILITY}).
	 */
	public ShuffleMutator() {
		this(RangeRandom.UNIFORM, DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Changes the order of the genes between two points randomly.
	 */
	@Override
	protected MutatorResult<Chromosome<G>> mutate(
		final Chromosome<G> chromosome,
		final double p,
		final RandomGenerator random
	) {
		final MutatorResult<Chromosome<G>> result;
		if (chromosome.length() > 1) {
			final var genes = MSeq.of(chromosome);
			final var range = _random.newRange(random, chromosome.length());

			genes.subSeq(range.a, range.b).shuffle();

			result = new MutatorResult<>(
				chromosome.newInstance(genes.toISeq()),
				range.b - range.a
			);
		} else {
			result = new MutatorResult<>(chromosome, 0);
		}

		return result;
	}

}
