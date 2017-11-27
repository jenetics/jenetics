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
package io.jenetics.ext;

import java.util.function.Predicate;
import java.util.function.Supplier;

import io.jenetics.Gene;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.EvolutionStreamable;
import io.jenetics.engine.Limits;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface EvolutionPool<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	public EvolutionPool<G, C> add(
		final EvolutionStreamable<G, C> streamable,
		final Supplier<Predicate<? super EvolutionResult<G, C>>> proceed
	);

	public default EvolutionPool<G, C> add(
		final EvolutionStreamable<G, C> engine,
		final long generations
	) {
		return add(engine, () -> Limits.byFixedGeneration(generations));
	}

	public default EvolutionPool<G, C>
	add(final EvolutionStreamable<G, C> streamable) {
		return add(streamable, () -> result -> true);
	}

	public EvolutionStream<G, C> stream();

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionPool<G, C> concat() {
		return new ConcatEvolutionPool<>();
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionPool<G, C> cycle() {
		return new CycleEvolutionPool<>();
	}

}
