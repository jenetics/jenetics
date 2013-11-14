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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.stat;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2013-11-14 $</em>
 * @since @__version__@
 */
class CollectorImpl<T, A, R>
	implements Collector<T, A, R>
{

	private final Supplier<A> _supplier;
	private final BiConsumer<A, T> _accumulator;
	private final BinaryOperator<A> _combiner;
	private final Function<A, R> _finisher;
	private final Set<Collector.Characteristics> _characteristics;

	CollectorImpl(
		final Supplier<A> supplier,
		final BiConsumer<A, T> accumulator,
		final BinaryOperator<A> combiner,
		final Function<A, R> finisher,
		final Set<Collector.Characteristics> characteristics
	) {
		_supplier = requireNonNull(supplier);
		_accumulator = requireNonNull(accumulator);
		_combiner = requireNonNull(combiner);
		_finisher = requireNonNull(finisher);
		_characteristics = unmodifiableSet(requireNonNull(characteristics));
	}

	@Override
	public Supplier<A> supplier() {
		return _supplier;
	}

	@Override
	public BiConsumer<A, T> accumulator() {
		return _accumulator;
	}

	@Override
	public BinaryOperator<A> combiner() {
		return _combiner;
	}

	@Override
	public Function<A, R> finisher() {
		return _finisher;
	}

	@Override
	public Set<Collector.Characteristics> characteristics() {
		return _characteristics;
	}
}
