package at.jku.dke.harmonic.optimizer.optimization.jenetics.jeneticsMO.jeneticsExtensions;

import io.jenetics.Chromosome;
import io.jenetics.Gene;
import io.jenetics.Mutator;
import io.jenetics.MutatorResult;
import io.jenetics.internal.math.Combinatorics;
import io.jenetics.util.MSeq;

import java.util.Random;

public class ArbitraryMutator<
        G extends Gene<?, G>,
        C extends Comparable<? super C>
        >
        extends Mutator<G, C> {

    public ArbitraryMutator(final double probability) {
        super(probability);
    }

    public ArbitraryMutator() {
        this(DEFAULT_ALTER_PROBABILITY);
    }

    @Override
    protected MutatorResult<Chromosome<G>> mutate(
            final Chromosome<G> chromosome,
            final double p,
            final Random random
    ) {
        final MutatorResult<Chromosome<G>> result;
        if(chromosome.length() > 1) {
            final int[] points = Combinatorics.subset(chromosome.length() +1, 2);
            final MSeq<G> genes = MSeq.of(chromosome);

            genes.subSeq(points[0], points[1]).shuffle();

            result = MutatorResult.of(
                    chromosome.newInstance(genes.toISeq()),
                    points[1] - points[0] - 1
            );
        } else {
            result = MutatorResult.of(chromosome);
        }
        return result;
    }
}
