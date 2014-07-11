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

import static org.jenetics.internal.math.statistics.max;
import static org.jenetics.internal.math.statistics.min;

import java.util.function.Consumer;
import java.util.stream.Collector;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-05-05 $</em>
 */
public class MinMax<C extends Comparable<? super C>> implements Consumer<C> {

	private C _min;
	private C _max;

	@Override
	public void accept(final C object) {
		_min = min(_min, object);
		_max = max(_max, object);
	}

	public void combine(final MinMax<C> other) {
		_min = min(_min, other._min);
		_max = max(_max, other._max);
	}

	public C getMin() {
		return _min;
	}

	public C getMax() {
		return _max;
	}

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
	 * @param <C> the type of the input elements
	 * @return a {@code Collector} implementing the moments-statistics reduction
	 * @throws java.lang.NullPointerException if the given {@code mapper} is
	 *         {@code null}
	 */
	public static <C extends Comparable<? super C>>
	Collector<C, ?, MinMax<C>> collector() {
		return Collector.of(
			MinMax::new,
			(r, t) -> r.accept(t),
			(a, b) -> {a.combine(b); return a;}
		);
	}

}
