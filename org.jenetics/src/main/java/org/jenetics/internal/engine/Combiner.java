package org.jenetics.internal.engine;

import org.jenetics.Gene;
import org.jenetics.Population;

public final class Combiner<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	public Result<Population<G, C>> combine(
		final Population<G, C> survivors,
		final Population<G, C> offspring
	) {
		return null;
	}
	
}
