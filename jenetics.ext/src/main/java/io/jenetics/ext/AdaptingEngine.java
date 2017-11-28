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

import static java.util.Objects.requireNonNull;

import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.Supplier;

import io.jenetics.Gene;
import io.jenetics.engine.EvolutionInit;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.EvolutionStreamable;
import io.jenetics.internal.engine.EvolutionStreamImpl;

import io.jenetics.ext.internal.GeneratorSpliterator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class AdaptingEngine<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements EvolutionStreamable<G, C>
{

	private final
	Function<EvolutionResult<G, C>, ? extends EvolutionStreamable<G, C>> _engine;

	public AdaptingEngine(
		final Function<EvolutionResult<G, C>, ? extends EvolutionStreamable<G, C>> engine
	) {
		_engine = requireNonNull(engine);
	}

	@Override
	public EvolutionStream<G, C>
	stream(final Supplier<EvolutionStart<G, C>> start) {
		return new EvolutionStreamImpl<G, C>(
			new GeneratorSpliterator<>(result -> generate(start, result)),
			false
		);
	}

	private Spliterator<EvolutionResult<G, C>>
	generate(
		final Supplier<EvolutionStart<G, C>> start,
		final EvolutionResult<G, C> result
	) {
		final EvolutionStart<G, C> es = result == null
			? start.get()
			: result.toEvolutionStart();

		return _engine.apply(result)
			.stream(es)
			.spliterator();
	}

	@Override
	public EvolutionStream<G, C> stream(final EvolutionInit<G> init) {
		return new EvolutionStreamImpl<G, C>(
			new GeneratorSpliterator<>(result -> generate(init, result)),
			false
		);
	}

	private Spliterator<EvolutionResult<G, C>>
	generate(
		final EvolutionInit<G> init,
		final EvolutionResult<G, C> result
	) {
		return result == null
			? _engine.apply(null)
				.stream(init)
				.spliterator()
			: _engine.apply(result)
				.stream(result.toEvolutionStart())
				.spliterator();
	}

}
