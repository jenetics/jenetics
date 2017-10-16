package io.jenetics.ext;

import java.util.Random;
import java.util.stream.IntStream;

import io.jenetics.AlterResult;
import io.jenetics.Chromosome;
import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.MutationResult;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.internal.math.probability;
import io.jenetics.internal.util.IntRef;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.Seq;

/**
 * Mutator implementation which is part of the
 * <a href="https://en.wikipedia.org/wiki/Weasel_program">Weasel program</a>
 * algorithm. The <i>Weasel program</i> is an thought experiment by Richard
 * Dawkins to illustrate the functioning of the evolution: random <i>mutation</i>
 * combined with non-random cumulative <i>selection</i>.
 * <p>
 * The mutator mutates the genes of <i>every</i> chromosome of <i>every</i>
 * genotype in the population with the given mutation probability.
 * </p>
 * {@link io.jenetics.engine.Engine} setup for the <i>Weasel program:</i>
 * <pre>{@code
 * final Engine<CharacterGene, Integer> engine = Engine
 *     .builder(fitness, gtf)
 *      // Set the 'WeaselSelector'.
 *     .selector(new WeaselSelector<>())
 *      // Disable survivors selector.
 *     .offspringFraction(1)
 *      // Set the 'WeaselMutator'.
 *     .alterers(new WeaselMutator<>(0.05))
 *     .build();
 * }</pre>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Weasel_program">Weasel program</a>
 * @see WeaselSelector
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.5
 * @version 3.5
 */
public class WeaselMutator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Mutator<G, C>
{

	public WeaselMutator(final double probability) {
		super(probability);
	}

	public WeaselMutator() {
		this(0.05);
	}

	@Override
	public AlterResult<G, C>
	alter(final Seq<Phenotype<G, C>> population, final long generation) {
		final Random random = RandomRegistry.getRandom();
		final Seq<MutationResult<Phenotype<G, C>>> result = population
			.map(pt -> mutate(pt, generation, _probability, random));

		return AlterResult.of(
			result.map(MutationResult::getResult).asISeq(),
			result.stream().mapToInt(MutationResult::getMutations).sum()
		);


		/*
		final IntRef alterations = new IntRef(0);

		final MSeq<Phenotype<G, C>> pop = MSeq.of(population);
		for (int i = 0; i < pop.size(); ++i) {
			final Phenotype<G, C> pt = pop.get(i);

			final Genotype<G> gt = pt.getGenotype();
			final Genotype<G> mgt = mutate(gt, alterations);
			final Phenotype<G, C> mpt = pt.newInstance(mgt, generation);
			pop.set(i, mpt);
		}

		return AlterResult.of(pop.toISeq(), alterations.value);
		*/
	}

	@Override
	protected MutationResult<Genotype<G>> mutate(
		final Genotype<G> genotype,
		final double p,
		final Random random
	) {
		final ISeq<MutationResult<Chromosome<G>>> result = genotype.toSeq()
			.map(gt -> mutate(gt, p, random));

		return MutationResult.of(
			Genotype.of(result.map(MutationResult::getResult)),
			result.stream().mapToInt(MutationResult::getMutations).sum()
		);

		/*
		indexes(RandomRegistry.getRandom(), genotype.length(), p)
			.mapToObj(i -> mutate2(genotype.toSeq(), i, p))
			.collect(ISeq.toISeq());

		final MSeq<Chromosome<G>> chromosomes = genotype.toSeq().copy();

		alterations.value +=
			indexes(RandomRegistry.getRandom(), genotype.length(), p)
				.map(i -> mutate(chromosomes, i, p))
				.sum();

		return genotype.newInstance(chromosomes.toISeq());
		*/
		//return null;
	}

	/*
		final Seq<MutationResult<Phenotype<G, C>>> result = population
			.map(pt -> random.nextInt() < P
				? mutate(pt, generation, p, random)
				: MutationResult.of(pt));

		return AlterResult.of(
			result.map(MutationResult::getResult).asISeq(),
			result.stream().mapToInt(MutationResult::getMutations).sum()
		);

	 */

//	private Genotype<G> mutate(
//		final Genotype<G> genotype,
//		final IntRef alterations
//	) {
//		final MSeq<Chromosome<G>> chromosomes = genotype.toSeq().copy();
//
//		alterations.value += IntStream.range(0, chromosomes.size())
//			.map(i -> mutate(chromosomes, i, _probability))
//			.sum();
//
//		return Genotype.of(chromosomes);
//	}

	/*
protected MutationResult<Genotype<G>> mutate(
		final Genotype<G> genotype,
		final double p,
		final Random random
	)
	 */

}
