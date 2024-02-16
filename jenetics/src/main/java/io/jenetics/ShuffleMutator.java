package io.jenetics;

import io.jenetics.internal.math.Subset;
import io.jenetics.util.MSeq;

import java.util.random.RandomGenerator;

/**
 * The arbitrary mutation, changes the oder of the genes between two randomly
 * chosen positions. The genes between the genes between the positions are shuffled.
 * This mutation operator can also be used for combinatorial problems,
 * where no duplicated genes within a chromosome are allowed, e.g., for the TSP.
 * For more details see: Kruse et al.. Elements of evolutionary algorithms. In Kruse et al., Computational
 * intelligence: A methodological introduction (pp. 255–285). Springer International Publishing.
 * <a href="https://doi.org/10.1007/978-3-030-42227-1_12">https://doi.org/10.1007/978-3-030-42227-1_12</a>
 *
 * @see Mutator
 * @see ArbitraryMutatorWithK
 *
 * @author <a href="mailto:feichtenschlager10@gmail.com">Paul Feichtenschlager</a>
 * @version 7.2
 * @since 7.2
 */

public class ShuffleMutator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Mutator<G, C>
{

	/**
	 * Constructs an alterer with a given recombination probability.
	 *
	 * @param probability the crossover probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}.
	 */
	public ShuffleMutator(final double probability) {
		super(probability);
	}

	/**
	 * Default constructor, with default mutation probability
	 * ({@link AbstractAlterer#DEFAULT_ALTER_PROBABILITY}).
	 */
	public ShuffleMutator() {
		this(DEFAULT_ALTER_PROBABILITY);
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
		if(chromosome.length() > 1) {
			final int[] points = Subset.next(random, chromosome.length() + 1, 2);
			final MSeq<G> genes = MSeq.of(chromosome);

			genes.subSeq(points[0], points[1]).shuffle();

			result = new MutatorResult<>(
				chromosome.newInstance(genes.toISeq()),
				points[1] - points[0]
			);
		} else {
			result = new MutatorResult<>(chromosome, 0);
		}
		return result;
	}
}
