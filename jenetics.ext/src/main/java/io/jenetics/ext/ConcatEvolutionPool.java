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
import java.util.stream.BaseStream;
import java.util.stream.Collectors;

import io.jenetics.Gene;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.internal.engine.EvolutionStreamImpl;
import io.jenetics.internal.util.ConcatSpliterator;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class ConcatEvolutionPool<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends AbstractEvolutionPool<G, C>
{

	public EvolutionStream<G, C> stream() {
		final AtomicReference<EvolutionStart<G, C>> start =
			new AtomicReference<>(EvolutionStart.of(ISeq.empty(), 1));

		final List<Spliterator<EvolutionResult<G, C>>> spliterators =
			_streamables.stream()
				.map(p -> p.streamable.apply(start::get)
					.limit(p.proceed)
					.peek(r -> start.set(r.next())))
				.map(BaseStream::spliterator)
				.collect(Collectors.toList());

		return new EvolutionStreamImpl<G, C>(
			new ConcatSpliterator<>(spliterators),
			false
		);
	}

}
