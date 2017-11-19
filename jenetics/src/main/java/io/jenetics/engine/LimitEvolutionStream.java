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

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
abstract class LimitEvolutionStream<T, S extends LimitEvolutionStream<T, S>>
	extends StreamProxy<T>
{

	private final Supplier<S> _creator;
	private final LimitSpliterator<T> _spliterator;

	LimitEvolutionStream(
		final Supplier<S> creator,
		final LimitSpliterator<T> spliterator,
		final boolean parallel
	) {
		super(StreamSupport.stream(spliterator, parallel));
		_creator = requireNonNull(creator);
		_spliterator = spliterator;
	}

	public S limit(final Predicate<? super T> proceed) {
		return null;// new LimitEvolutionStream<>(_spliterator.limit(proceed), isParallel());
	}

}
