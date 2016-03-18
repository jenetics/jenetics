package org.jenetix;

import java.util.stream.IntStream;

import org.jenetics.Chromosome;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.internal.util.IntRef;
import org.jenetics.util.MSeq;

/**
 * @author Franz Wilhelmstötter <franz.wilhelmstoetter@emarsys.com>
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
	public int alter(final Population<G, C> population, final long generation) {
		final IntRef alterations = new IntRef(0);

		for (int i = 0; i < population.size(); ++i) {
			final Phenotype<G, C> pt = population.get(i);

			final Genotype<G> gt = pt.getGenotype();
			final Genotype<G> mgt = mutate(gt, alterations);
			// Is package private.
			final Phenotype<G, C> mpt = null; //pt.newInstance(mgt, generation);
			population.set(i, mpt);
		}

		return alterations.value;
	}

	private Genotype<G> mutate(
		final Genotype<G> genotype,
		final IntRef alterations
	) {
		final MSeq<Chromosome<G>> chromosomes = genotype.toSeq().copy();

		alterations.value += IntStream.range(0, chromosomes.size())
			.map(i -> mutate(chromosomes, i))
			.sum();

		// Is package private.
		return null; //genotype.newInstance(chromosomes.toISeq());
	}

	private int mutate(final MSeq<Chromosome<G>> c, final int index) {
		final Chromosome<G> chromosome = c.get(index);
		final MSeq<G> genes = chromosome.toSeq().copy();

		final int mutations = mutate(genes, _probability);
		if (mutations > 0) {
			c.set(index, chromosome.newInstance(genes.toISeq()));
		}
		return mutations;
	}

}
