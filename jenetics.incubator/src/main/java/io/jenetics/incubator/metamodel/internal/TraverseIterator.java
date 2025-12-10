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
package io.jenetics.incubator.metamodel.internal;

import static java.util.Objects.requireNonNull;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public abstract class TraverseIterator<S, T> implements Iterator<T>  {

	final Dtor<? super S, ? extends T> dtor;
	final Function<? super T, ? extends S> mapper;
	final Function<? super S, ?> unwrapper;

	TraverseIterator(
		final Dtor<? super S, ? extends T> dtor,
		final Function<? super T, ? extends S> mapper,
		final Function<? super S, ?> unwrapper
	) {
		this.dtor = requireNonNull(dtor);
		this.mapper = requireNonNull(mapper);
		this.unwrapper = requireNonNull(unwrapper);
	}

	/**
	 * Creates a {@code Stream} from {@code this} iterator.
	 *
	 * @return a {@code Stream} from {@code this} iterator
	 */
	public Stream<T> asStream() {
		return StreamSupport.stream(
			spliteratorUnknownSize(this, Spliterator.SIZED),
			false
		);
	}

}
