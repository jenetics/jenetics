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
package io.jenetics.util;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.stream.Gatherer;

/**
 * This class contains factory methods for stream {@link Gatherer} instances.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 9.1
 * @version 9.1
 */
public final class Gatherers {
	private Gatherers() {
	}

	/**
	 * Return a new gatherer, which guarantees a strictly increasing stream, from
	 * an arbitrarily ordered source stream. Note that this gatherer doesn't sort
	 * the stream. It <em>just</em> skips the <em>out of order</em> elements.
	 *
	 * <pre>{@code
	 *     +----3--2--5--4--7--7--4--9----|
	 *        toStrictlyIncreasing()
	 *     +----3-----5-----7--------9----|
	 * }</pre>
	 *
	 * {@snippet lang="java":
	 * final ISeq<Integer> values = new Random().ints(0, 100)
	 *     .boxed()
	 *     .limit(100)
	 *     .gather(Gatherers.toStrictlyIncreasing())
	 *     .collect(ISeq.toISeq());
	 *
	 * System.out.println(values);
	 * // [6,47,65,78,96,96,99]
	 * }
	 *
	 * @param <C> the comparable type
	 * @return a new gatherer
	 */
	public static <C extends Comparable<? super C>> Gatherer<C, ?, C>
	toStrictlyIncreasing() {
		return toStrictlyImproving(Comparator.naturalOrder());
	}

	/**
	 * Return a new gatherer, which guarantees a strictly improving stream, from
	 * an arbitrarily ordered source stream. Note that this gatherer doesn't sort
	 * the stream. It <em>just</em> skips the <em>out of order</em> elements.
	 * {@snippet lang="java":
	 * final ISeq<Integer> values = new Random().ints(0, 100)
	 *     .boxed()
	 *     .limit(100)
	 *     .gather(Gatherers.toStrictlyImproving(Comparator.naturalOrder()))
	 *     .collect(ISeq.toISeq());
	 *
	 * System.out.println(values);
	 * // [6,47,65,78,96,96,99]
	 * }
	 *
	 * @param <T> the element type
	 * @param comparator the comparator used for testing the elements
	 * @return a new gatherer
	 * @throws NullPointerException if the given {@code comparator} is
	 *         {@code null}
	 */
	public static <T> Gatherer<T, ?, T>
	toStrictlyImproving(final Comparator<? super T> comparator) {
		requireNonNull(comparator);
		return Gatherer.ofSequential(
			State<T>::new,
			(state, value, downstream) -> {
				final T best = best(comparator, state.best, value);
				if (best != state.best) {
					state.best = best;
					return downstream.push(best);
				}
				return true;
			}
		);
	}

	private static final class State<T> {
		private T best;
	}

	private static <T>
	T best(final Comparator<? super T> comparator, final T a, final T b) {
		if (a == null && b == null) return null;
		if (a == null) return b;
		if (b == null) return a;
		return comparator.compare(a, b) >= 0 ? a : b;
	}

}
