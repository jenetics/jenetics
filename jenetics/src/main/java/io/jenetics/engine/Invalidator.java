/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.engine;

import java.util.concurrent.atomic.AtomicBoolean;

import io.jenetics.Gene;
import io.jenetics.Phenotype;

/**
 *
 * @param <G>
 * @param <C>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public final class Invalidator<
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
			start.population().map(Phenotype::nullifyFitness),
			start.generation()
		);
	}

	public void invalid() {
		_invalid.set(true);
	}

}
