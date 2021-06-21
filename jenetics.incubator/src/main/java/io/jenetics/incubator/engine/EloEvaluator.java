package io.jenetics.incubator.engine;

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.engine.Evaluator;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

public class EloEvaluator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Evaluator<G, C>
{

	@Override
	public ISeq<Phenotype<G, C>> eval(final Seq<Phenotype<G, C>> population) {
		return null;
	}

}
