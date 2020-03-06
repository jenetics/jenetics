package io.jenetics.engine;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

import io.jenetics.Gene;
import io.jenetics.Phenotype;

public class Invalidator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements EvolutionInterceptor<G, C>
{

	private final AtomicBoolean _invalid = new AtomicBoolean(false);

	@Override
	public EvolutionStart<G, C> before(final EvolutionStart<G, C> start) {
		final boolean invalid = _invalid.getAndSet(false);
		return invalid ? invalidate(start) : start;
	}

	private EvolutionStart<G, C> invalidate(final EvolutionStart<G, C> start) {
		return EvolutionStart.of(
			start.population().map(Phenotype::discard),
			start.generation()
		);
	}

	public void invalid() {
		_invalid.set(true);
	}

}
