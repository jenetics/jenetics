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
package io.jenetics.internal.engine;

import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import io.jenetics.Gene;
import io.jenetics.engine.Evolution;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.internal.util.LimitSpliterator;
import io.jenetics.internal.util.StreamProxy;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 5.1
 */
public final class EvolutionStreamImpl<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends StreamProxy<EvolutionResult<G, C>>
	implements EvolutionStream<G, C>
{

	private final Spliterator<EvolutionResult<G, C>> _spliterator;

	public EvolutionStreamImpl(
		final Spliterator<EvolutionResult<G, C>> spliterator,
		final boolean parallel
	) {
		super(StreamSupport.stream(spliterator, parallel));
		_spliterator = spliterator;
	}

	public EvolutionStreamImpl(
		final Supplier<EvolutionStart<G, C>> start,
		final Evolution<G, C> evolution
	) {
		this(new EvolutionSpliterator<>(start, evolution), false);
	}

	@Override
	public EvolutionStream<G, C>
	limit(final Predicate<? super EvolutionResult<G, C>> proceed) {
		return new EvolutionStreamImpl<>(
			LimitSpliterator.of(_spliterator, proceed),
			isParallel()
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionStreamImpl<G, C> of(
		final Supplier<EvolutionStart<G, C>> start,
		final Function<
			? super EvolutionStart<G, C>,
			? extends Evolution<G, C>> evolution
	) {
		return new EvolutionStreamImpl<G, C>(
			EvolutionSpliterator.of(start, evolution),
			false
		);
	}

}
