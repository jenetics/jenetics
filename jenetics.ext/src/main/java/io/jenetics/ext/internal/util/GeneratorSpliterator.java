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
package io.jenetics.ext.internal.util;

import static java.util.Objects.requireNonNull;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
public class GeneratorSpliterator<T> implements Spliterator<T> {

	private final Function<? super T, ? extends Spliterator<T>> _generator;

	private Spliterator<T> _current;
	private T _element;

	public GeneratorSpliterator(
		final Function<? super T, ? extends Spliterator<T>> generator
	) {
		_generator = requireNonNull(generator);
	}

	@Override
	public boolean tryAdvance(final Consumer<? super T> action) {
		requireNonNull(action);

		final boolean advance = spliterator().tryAdvance(element -> {
			action.accept(element);
			_element = element;
		});

		if (!advance) {
			_current = null;
		}

		return true;
	}

	@Override
	public Spliterator<T> trySplit() {
		return new GeneratorSpliterator<>(_generator);
	}

	@Override
	public long estimateSize() {
		return Long.MAX_VALUE;
	}

	@Override
	public int characteristics() {
		return Spliterator.ORDERED;
	}

	private Spliterator<T> spliterator() {
		if (_current == null) {
			_current = _generator.apply(_element);
		}

		return _current;
	}

}
