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

import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.jenetics.Gene;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.internal.engine.EvolutionStreamImpl;
import io.jenetics.util.ISeq;

import io.jenetics.ext.internal.CycleSpliterator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class CycleEvolutionPool<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends AbstractEvolutionPool<G, C>
{

	@Override
	public EvolutionStream<G, C> stream() {
		final AtomicReference<EvolutionStart<G, C>> start =
			new AtomicReference<>(EvolutionStart.of(ISeq.empty(), 1));

		final List<Supplier<Spliterator<EvolutionResult<G, C>>>> spliterators =
			_streamables.stream()
				.map(engine -> toSpliterator(engine, start))
				.collect(Collectors.toList());

		return new EvolutionStreamImpl<G, C>(
			new CycleSpliterator<>(spliterators),
			false
		);
	}

	private Supplier<Spliterator<EvolutionResult<G, C>>> toSpliterator(
		final EngineLimit<G, C> engine,
		final AtomicReference<EvolutionStart<G, C>> start
	) {
		return () -> engine.engine.stream(start::get)
			.limit(engine.proceed.get())
			.peek(result -> start.set(result.toEvolutionStart()))
			.spliterator();
	}

}
