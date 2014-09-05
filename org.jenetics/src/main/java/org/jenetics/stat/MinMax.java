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

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.math.statistics.max;
import static org.jenetics.internal.math.statistics.min;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.stream.Collector;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-09-05 $</em>
 */
public final class MinMax<C> implements Consumer<C> {

	private final Comparator<? super C> _comparator;
	private C _min;
	private C _max;

	private MinMax(final Comparator<? super C> comparator) {
		_comparator = requireNonNull(comparator);
	}

	@Override
	public void accept(final C object) {
		_min = min(_comparator, _min, object);
		_max = max(_comparator, _max, object);
	}

	public MinMax<C> combine(final MinMax<C> other) {
		_min = min(_comparator, _min, other._min);
		_max = max(_comparator, _max, other._max);

		return this;
	}

	public C getMin() {
		return _min;
	}

	public C getMax() {
		return _max;
	}



	/* *************************************************************************
	 *  Some static factory methods.
	 * ************************************************************************/

	/**
	 * Return a {@code Collector} which applies an long-producing mapping
	 * function to each input element, and returns moments-statistics for the
	 * resulting values.
	 *
	 * [code]
	 * final Stream&lt;SomeObject&gt; stream = ...
	 * final MinMax&lt;SomeObject&gt; moments = stream
	 *     .collect(doubleMoments.collector());
	 * [/code]
	 *
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} implementing the moments-statistics reduction
	 * @throws java.lang.NullPointerException if the given {@code mapper} is
	 *         {@code null}
	 */
	public static <T> Collector<T, ?, MinMax<T>>
	collector(final Comparator<? super T> comparator) {
		return Collector.of(
			() -> MinMax.of(comparator),
			MinMax::accept,
			MinMax::combine
		);
	}

	/**
	 * Create a new {@code MinMax} <i>consumer</i> with the given
	 * {@link java.util.Comparator}.
	 *
	 * @param comparator the comparator used for comparing two elements
	 * @param <T> the element type
	 * @return a new {@code MinMax} <i>consumer</i>
	 * @throws java.lang.NullPointerException if the {@code comparator} is
	 *         {@code null}.
	 */
	public static <T> MinMax<T> of(final Comparator<? super T> comparator) {
		return new MinMax<>(comparator);
	}

	/**
	 * Create a new {@code MinMax} <i>consumer</i>.
	 *
	 * @param <C> the element type
	 * @return a new {@code MinMax} <i>consumer</i>
	 */
	public static <C extends Comparable<? super C>> MinMax<C> of() {
		return of((a, b) -> a.compareTo(b));
	}

}
