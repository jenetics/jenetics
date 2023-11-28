package io.jenetics;

import io.jenetics.util.MSeq;

import java.util.random.RandomGenerator;

/**
 * The arbitrary mutation, changes the oder of the genes between two randomly
 * chosen positions. The genes between the genes between the positions are shuffled.
 * This mutation operator can also be used for combinatorial problems,
 * where no duplicated genes within a chromosome are allowed, e.g., for the TSP. In
 * contrast to the ArbitraryMutation, the number of genes which are mutated has an
 * expectancy value of p. Hereby a random value with a shifted expectancy value is
 * used.
 * For more details see: Kruse, R., Mostaghim, S., Borgelt, C., Braune, C., &
 * Steinbrecher, M. (2022a). Elements of evolutionary algorithms. In R. Kruse,
 * S. Mostaghim, C. Borgelt, C. Braune, & M. Steinbrecher (Eds.), Computational
 * intelligence: A methodological introduction (pp. 255–285). Springer International Publishing.
 * <a href="https://doi.org/10.1007/978-3-030-42227-1_12">https://doi.org/10.1007/978-3-030-42227-1_12</a>
 *
 * @see Mutator
 * @see ArbitraryMutator
 *
 * @author <a href="mailto:feichtenschlager10@gmail.com">Paul Feichtenschlager</a>
 * @version 7.2
 * @since 7.2
 */

public class ArbitraryMutatorWithK<
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
	public ArbitraryMutatorWithK(final double probability) {
		super(probability);
	}

	/**
	 * Default constructor, with default mutation probability
	 * ({@link AbstractAlterer#DEFAULT_ALTER_PROBABILITY}).
	 */
	public ArbitraryMutatorWithK() {
		this(DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Changes the order of the genes between two points randomly. The distance between
	 * the two positions has an expectancy value equal p.
	 */
	@Override
	protected MutatorResult<Chromosome<G>> mutate(
		final Chromosome<G> chromosome,
		final double p,
		final RandomGenerator random
	) {
		final MutatorResult<Chromosome<G>> result;
		if(chromosome.length() > 1) {
			double lengthDouble = getDistance(p, random);
			int lengthInt = (int) Math.round(chromosome.length()*lengthDouble);
			if(lengthInt == 0) {
				result = new MutatorResult<>(chromosome, 0);
			} else {
				int startingPoint;
				if (lengthInt >= chromosome.length()) {
					startingPoint = 0;
				} else {
					startingPoint = random.nextInt(chromosome.length() - lengthInt);
				}

				final MSeq<G> genes = MSeq.of(chromosome);
				genes.subSeq(startingPoint, startingPoint + lengthInt).shuffle();

				result = new MutatorResult<>(
					chromosome.newInstance(genes.toISeq()),
					lengthInt
				);
			}
		} else {
			result = new MutatorResult<>(chromosome, 0);
		}
		return result;
	}

	private double getDistance(double p, RandomGenerator random) {
		double r = random.nextDouble();
		if(p == 0) {
			return 0;
		} else if(p < 0.292893) {
			double b = (2-Math.sqrt(2))/p;
			double m = -Math.pow(b,2)/2;
			return (-b+Math.sqrt(Math.pow(b,2)+2*r*m))/m;
		} else if(p < 0.5){
			double b = (Math.pow(p,2)-0.5)/(Math.pow(p,2)-p);
			double m = 2-2*b;
			return (-b+Math.sqrt(Math.pow(b,2)+2*r*m))/m;
		} else if(p == 0.5) {
			return r;
		} else if(p < 0.707107) {
			double b = (Math.pow(p,2)-0.5)/(Math.pow(p, 2)- p);
			double m = 2-2*b;
			return (b-Math.sqrt(Math.pow(b,2)+2*m*(r-1+b+0.5*m)))/-m;
		} else if(p < 1) {
			double b = 0.5*(1-Math.pow(((2-Math.sqrt(2))/(1-p)-1), 2));
			double m = -b+Math.sqrt(1-2*b)+1;
			return (b-Math.sqrt(Math.pow(b,2)+2*m*(r-1+b+0.5*m)))/-m;
		} else {
			return 1;
		}
	}
}
