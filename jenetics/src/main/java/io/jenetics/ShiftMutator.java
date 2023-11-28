package io.jenetics;

import io.jenetics.internal.math.Subset;
import io.jenetics.util.MSeq;

import java.util.random.RandomGenerator;


/**
 * The shift mutation, applies mutation between two randomly chosen points.
 * A random value between the two points splits the sequences of genes
 * between the positions. The second sequence is then shifted in front of
 * the first one. This mutation operator can also be used for combinatorial problems,
 * where no duplicated genes within a chromosome are allowed, e.g., for the TSP.
 * For more details see: Kruse, R., Mostaghim, S., Borgelt, C., Braune, C., &
 * Steinbrecher, M. (2022a). Elements of evolutionary algorithms. In R. Kruse,
 * S. Mostaghim, C. Borgelt, C. Braune, & M. Steinbrecher (Eds.), Computational
 * intelligence: A methodological introduction (pp. 255–285). Springer International Publishing.
 * <a href="https://doi.org/10.1007/978-3-030-42227-1_12">https://doi.org/10.1007/978-3-030-42227-1_12</a>
 *
 * @see Mutator
 * @see ShiftMutatorWithK
 *
 * @author <a href="mailto:feichtenschlager10@gmail.com">Paul Feichtenschlager</a>
 * @version 7.2
 * @since 7.2
 */
public class ShiftMutator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
	>
	extends Mutator<G, C> {

	/**
	 * Constructs an alterer with a given recombination probability.
	 *
	 * @param probability the crossover probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}.
	 */
	public ShiftMutator(final double probability) {
		super(probability);
	}

	/**
	 * Default constructor, with default mutation probability
	 * ({@link AbstractAlterer#DEFAULT_ALTER_PROBABILITY}).
	 */
	public ShiftMutator() {
		this(DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Splits the values between two points into two sequences and shits the second
	 * one in front of the first one.
	 */
	@Override
	protected MutatorResult<Chromosome<G>> mutate(
		final Chromosome<G> chromosome,
		final double p,
		final RandomGenerator random
	) {
		final MutatorResult<Chromosome<G>> result;
		if(chromosome.length() > 1) {
			final int[] points = Subset.next(random, chromosome.length() + 1, 3);
			final MSeq<G> genes = MSeq.of(chromosome);
			MSeq<G> firstSeq = genes.subSeq(points[0], points[1]).copy();
			int difOne = points[2] - points[1];
			MSeq<G> secondSeq = genes.subSeq(points[1], points[2]).copy();
			int difTwo = points[1] - points[0];
			int i = 0;
			for(G g : firstSeq) {
				genes.set(points[0]+i+difOne, g);
				i++;
			}
			i = 0;
			for(G g : secondSeq) {
				genes.set(points[1]+i-difTwo,g);
				i++;
			}

			result =  new MutatorResult<>(
				chromosome.newInstance(genes.toISeq()),
				points[2] - points[0] - 1
			);
		} else {
			result = new MutatorResult<>(chromosome, 0);
		}
		return result;
	}
}
