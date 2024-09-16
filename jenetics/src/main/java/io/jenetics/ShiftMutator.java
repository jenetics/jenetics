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
 * The shift mutation applies mutation between two randomly chosen points. A
 * random value between the two points splits the sequences of genes between the
 * positions. The second sequence is then shifted in front of the first one.
 * This mutation operator can be used for combinatorial problems, where no
 * duplicated genes within a chromosome are allowed, e.g., for the TSP.
 * <p>
 *	<img src="doc-files/ShiftMutator.svg" width="450"
 *	     alt="Shift mutator" >
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
public class ShiftMutator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Mutator<G, C>
{

	/**
	 * This class defines the {@link Chromosome} shift indexes.
	 * <p>
	 *	<img src="doc-files/ShiftMutator.svg" width="450"
	 *	     alt="Shift mutator" >
	 *
	 * @param a the start index of the shift range (inclusively)
	 * @param b the end index (exclusively) of the first shift range and the
	 *        start index (inclusively) of the second shift range
	 * @param c the end index (exclusively) of the second shift range
	 */
	public record Range(int a, int b, int c) {

		/**
		 * Create a new shift range with the given parameters
		 *
		 * @throws IllegalArgumentException if the indexes are smaller than zero
		 *         or overlapping
		 */
		public Range {
			if (a < 0 || b < 0 || c < 0 || a >= b || b >= c) {
				throw new IllegalArgumentException(
					"Invalid shift range [%d, %d, %d].".formatted(a, b, c)
				);
			}
		}

		/**
		 * Performs the <em>shifting</em> of the elements of the given
		 * {@code sequence}.
		 *
		 * @param seq the elements which will be shifted
		 * @param <G> the shifted element type
		 * @throws NullPointerException if the given {@code sequence} is
		 *         {@code null}
		 */
		<G> void shift(final MSeq<G> seq) {
			if (seq.length() < c) {
				throw new IllegalArgumentException(
					"Sequence to short for given range: [length=%d, %s]."
						.formatted(seq.length(), this)
				);
			}

			final var temp = seq.subSeq(a, b).copy();

			final var length1 = b - a;
			final var length2 = c - b;
			for (int i = 0; i < length2; ++i) {
				seq.set(a + i, seq.get(b + i));
			}
			for (int i = 0; i < length1; ++i) {
				seq.set(a + length2 + i, temp.get(i));
			}
		}

	}

	/**
	 * Functional interface for creating random shift ranges objects for
	 * shifting sequences of a given length.
	 */
	@FunctionalInterface
	public interface RangeRandom {

		/**
		 * Random shift range generator, which creates shifter with uniformly
		 * distributed shifting points. Both the length and the shift indexes
		 * are chosen uniformly.
		 */
		RangeRandom UNIFORM = (random, length) -> {
			if (length <= 1) {
				throw new IllegalArgumentException(
					"Length must be greater then 1, but was %d."
						.formatted(length)
				);
			}

			final int[] points = Subsets.next(random, length + 1, 3);
			return new Range(points[0], points[1], points[2]);
		};

		/**
		 * Create a new <em>random</em> shift range for shifting sequences with a
		 * given {@code length}.
		 *
		 * @param random the random generator to be used for creating a new
		 *        shifter
		 * @param length the length of the sequence to be shifted
		 * @return a new <em>randomly</em> created shifter
		 */
		Range newRange(final RandomGenerator random, final int length);

		/**
		 * Create a new random shift range generator, which uses the given
		 * distributions for creating the shift points.
		 *
		 * @param lengthSampler the sampler used for creating the shifted gene
		 *        count
		 * @param indexSampler the sampler used for creating the shift indexes
		 * @return a new random shift generator with the given parameters
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

				final int lng = lengthSampler.sample(random, IntRange.of(2, size));
				final int a = indexSampler.sample(random, IntRange.of(0, size - lng));
				final int b = indexSampler.sample(random, IntRange.of(a + 1, lng - 1));
				final int c = a + lng;

				return new Range(a, b, c);
			};
		}

		/**
		 * Create a new random shift range generator, which uses the given
		 * distributions for creating the shift points. The shift indexes are
		 * uniformly distributed.
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
	 * Constructs an alterer with a given shifter-random and recombination
	 * probability.
	 *
	 * @param probability the crossover probability.
	 * @param random the {@link Range} generator.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}.
	 */
	public ShiftMutator(final RangeRandom random, final double probability) {
		super(probability);
		_random = requireNonNull(random);
	}

	/**
	 * Constructs an alterer with a given shifter-random.
	 *
	 * @param random the {@link Range} generator.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}.
	 */
	public ShiftMutator(final RangeRandom random) {
		this(random, DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Constructs an alterer with a given recombination probability.
	 *
	 * @param probability the crossover probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}.
	 */
	public ShiftMutator(final double probability) {
		this(RangeRandom.UNIFORM, probability);
	}

	/**
	 * Default constructor, with default mutation probability
	 * ({@link AbstractAlterer#DEFAULT_ALTER_PROBABILITY}).
	 */
	public ShiftMutator() {
		this(DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Splits the values between two points into two sequences and shifts the
	 * second one in front of the first one.
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

			range.shift(genes);

			result = new MutatorResult<>(
				chromosome.newInstance(genes.toISeq()),
				range.c - range.a
			);
		} else {
			result = new MutatorResult<>(chromosome, 0);
		}

		return result;
	}

}
