package io.jenetics.ext;

import java.util.Random;

import io.jenetics.AltererResult;
import io.jenetics.Chromosome;
import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.MutationResult;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.util.ISeq;
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
	public AltererResult<G, C>
	alter(final Seq<Phenotype<G, C>> population, final long generation) {
		final Random random = RandomRegistry.getRandom();
		final Seq<MutationResult<Phenotype<G, C>>> result = population
			.map(pt -> mutate(pt, generation, _probability, random));

		return AltererResult.of(
			result.map(MutationResult::getResult).asISeq(),
			result.stream().mapToInt(MutationResult::getMutations).sum()
		);
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
	}

}
