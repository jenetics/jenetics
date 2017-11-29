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

import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;

import io.jenetics.Gene;
import io.jenetics.engine.EvolutionInit;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.EvolutionStreamable;
import io.jenetics.internal.engine.EvolutionStreamImpl;

import io.jenetics.ext.internal.ConcatSpliterator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ConcatEnginePool<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends EnginePool<G, C>
{

	public ConcatEnginePool(
		final List<? extends EvolutionStreamable<G, C>> engines
	) {
		super(engines);
	}

	@Override
	public EvolutionStream<G, C>
	stream(final Supplier<EvolutionStart<G, C>> start) {
		final AtomicReference<EvolutionStart<G, C>> other =
			new AtomicReference<>(null);

		final List<Spliterator<EvolutionResult<G, C>>> spliterators =
			_engines.stream()
				.map(engine -> engine
					.stream(() -> start(start, other))
					.peek(result -> other.set(result.toEvolutionStart())))
				.map(BaseStream::spliterator)
				.collect(Collectors.toList());

		return new EvolutionStreamImpl<G, C>(
			new ConcatSpliterator<>(spliterators),
			false
		);
	}

	private EvolutionStart<G, C> start(
		final Supplier<EvolutionStart<G, C>> first,
		final AtomicReference<EvolutionStart<G, C>> other
	) {
		return other.get() != null ? other.get() : first.get();
	}

	@Override
	public EvolutionStream<G, C> stream(final EvolutionInit<G> init) {
		return null;
	}


	@SafeVarargs
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	ConcatEnginePool<G, C> of(final EvolutionStreamable<G, C>... engines) {
		return new ConcatEnginePool<>(Arrays.asList(engines));
	}


}
