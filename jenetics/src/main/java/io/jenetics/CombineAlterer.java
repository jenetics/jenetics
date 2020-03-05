package io.jenetics;

import java.util.function.BinaryOperator;

import io.jenetics.util.MSeq;
import io.jenetics.util.Mean;

public class CombineAlterer<
	G extends Gene<?, G> & Mean<G>,
	C extends Comparable<? super C>
>
	extends Recombinator<G, C>
{

	public CombineAlterer(final BinaryOperator<G> combiner, final double probability) {
		super(probability, 2);
	}

	@Override
	protected int recombine(MSeq<Phenotype<G, C>> population, int[] individuals, long generation) {
		return 0;
	}
}
