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
package io.jenetics.ext.internal;

import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.LimitSpliterator.TRUE;
import static io.jenetics.internal.util.LimitSpliterator.and;

import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import io.jenetics.internal.util.LimitSpliterator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class GeneratedSpliterator<T> implements LimitSpliterator<T> {

	private final Predicate<? super T> _proceed;
	private final Function<? super T, ? extends Spliterator<T>> _spliterator;

	private Spliterator<T> _current;
	private T _element;

	public GeneratedSpliterator(
		final Predicate<? super T> proceed,
		final Function<? super T, ? extends Spliterator<T>> spliterator
	) {
		_proceed = requireNonNull(proceed);
		_spliterator = requireNonNull(spliterator);
	}

	public GeneratedSpliterator(final Function<? super T, ? extends Spliterator<T>> spliterator) {
		this(TRUE(), spliterator);
	}

	@Override
	public GeneratedSpliterator<T> limit(final Predicate<? super T> proceed) {
		return new GeneratedSpliterator<>(and(_proceed, proceed), _spliterator);
	}

	@Override
	public boolean tryAdvance(final Consumer<? super T> action) {
		requireNonNull(action);

		final AtomicBoolean proceed = new AtomicBoolean(true);
		final boolean advance = spliterator().tryAdvance(t -> {
			proceed.set(_proceed.test(t));
			action.accept(t);
			_element = t;
		});

		if (!advance) {
			_current = null;
		}

		return proceed.get();
	}
	@Override
	public Spliterator<T> trySplit() {
		return null;
	}

	@Override
	public long estimateSize() {
		return spliterator().estimateSize();
	}

	@Override
	public int characteristics() {
		return spliterator().characteristics();
	}

	private Spliterator<T> spliterator() {
		if (_current == null) {
			_current = _spliterator.apply(_element);
		}

		return _current;
	}

}
