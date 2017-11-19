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

import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import io.jenetics.Gene;
import io.jenetics.internal.util.JoinedSpliterator;
import io.jenetics.internal.util.StreamProxy;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
class JoinedEvolutionStream<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends StreamProxy<EvolutionResult<G, C>>
	implements EvolutionStream<G, C>
{

	private final Predicate<? super EvolutionResult<G, C>> _proceed;
	private final JoinedSpliterator<EvolutionResult<G, C>> _spliterator;

	JoinedEvolutionStream(
		final Predicate<? super EvolutionResult<G, C>> proceed,
		final JoinedSpliterator<EvolutionResult<G, C>> spliterator
	) {
		super(StreamSupport.stream(spliterator, false));
		_proceed = proceed;
		_spliterator = spliterator;
	}

	@Override
	public EvolutionStream<G, C>
	limit(final Predicate<? super EvolutionResult<G, C>> proceed) {
		final Predicate<? super EvolutionResult<G, C>> prcd = _proceed == TRUE
			? proceed
			: r -> proceed.test(r) & _proceed.test(r);

		return new JoinedEvolutionStream<>(
			prcd,
			_spliterator
		);
	}

	private static final Predicate<?> TRUE = a -> true;

	@SuppressWarnings("unchecked")
	private static <T> Predicate<T> TRUE() {
		return (Predicate<T>)TRUE;
	}

}
