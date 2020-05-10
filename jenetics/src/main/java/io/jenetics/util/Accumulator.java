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

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * This interface lets you accumulate elements of type {@code T} to a result of
 * type {@code R}. It can be seen as a continuous {@link Collector}. In contrast
 * to a {@link Collector} an {@code Accumulator} can deliver intermediate results
 * while accumulating. Due to the similarities between the {@link Collector}
 * class, an {@code Accumulator} can be created from any {@link Collector}
 * implementation ({@link #of(Collector)}).
 *
 * <pre>{@code
 * final Accumulator<Integer, List<Integer>> accu =
 *     Accumulator.of(Collectors.toList());
 *
 * final ISeq<List<Integer>> result = IntStream.range(0, 10).boxed()
 *     .peek(accu)
 *     .map(i -> accu.result())
 *     .collect(ISeq.toISeq());
 *
 * result.forEach(System.out::println);
 * }</pre>
 * The code above gives you the following output.
 * <pre>
 * [0]
 * [0,1]
 * [0,1,2]
 * [0,1,2,3]
 * [0,1,2,3,4]
 * [0,1,2,3,4,5]
 * [0,1,2,3,4,5,6]
 * [0,1,2,3,4,5,6,7]
 * [0,1,2,3,4,5,6,7,8]
 * [0,1,2,3,4,5,6,7,8,9]
 * </pre>
 *
 * @apiNote
 * In contrast to the {@link Collector} interface, the {@code Accumulator} is
 * not mergeable.
 * The <em>accumulator</em> is not thread-safe and can't be used for parallel
 * streams.
 *
 * @param <T> the type of input elements to the accumulate operation
 * @param <A> the accumulator type
 * @param <R> the result type of the accumulated operation
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Accumulator<T, A extends Accumulator<T, A, R>, R>
	extends Consumer<T>, Collector<T, A, R>
{

	/**
	 * Return a <em>copy</em>  of the current result of the accumulated elements.
	 * The accumulated elements are not changed by this method.
	 *
	 * @return the current result of the accumulated elements
	 */
	R result();

	default A combine(final A other) {
		throw new UnsupportedOperationException();
	}

	@Override
	default BiConsumer<A, T> accumulator() {
		return A::accept;
	}

	@Override
	default BinaryOperator<A> combiner() {
		return A::combine;
	}

	@Override
	default Function<A, R> finisher() {
		return A::result;
	}

	@Override
	default Set<Characteristics> characteristics() {
		return Set.of();
	}

	/**
	 * Create a new accumulator from the given {@code collector}.
	 *
	 * <pre>{@code
	 * final Accumulator<Integer, ISeq<Integer>> accu =
	 *     Accumulator.of(ISeq.toISeq());
	 * }</pre>
	 *
	 * @param collector the collector which is used for accumulation and creation
	 *        the result value.
	 * @param <T> the type of input elements to the reduction operation
	 * @param <R> the result type of the reduction operation
	 * @return a new accumulator which is backed by the given {@code collector}
	 * @throws NullPointerException if the given {@code collector} is {@code null}
	 */
	static <T, R> Accumulator<T, ?, R>
	of(final Collector<T, ?, R> collector) {
		return new CollectorAccumulator<>(collector);
	}

	/**
	 * Returns a synchronized (thread-safe) accumulator backed by the specified
	 * {@code accumulator}. The given {@code lock} object is used for
	 * synchronization.
	 *
	 * @param accumulator the accumulator to be "wrapped" in a synchronized
	 *        accumulator
	 * @param lock the <em>lock</em> used for synchronization
	 * @param <T> the type of input elements to the accumulate operation
	 * @param <R> the result type of the accumulated operation
	 * @return a synchronized view of the specified accumulator
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	static <T, R> Accumulator<T, ?, R> sync(
		final Accumulator<T, ?, R> accumulator,
		final Object lock
	) {
		@SuppressWarnings({"rawtypes", "unchecked"})
		final Accumulator<T, ?, R> result = new SynchronizedAccumulator(
			accumulator,
			lock
		);
		return result;
	}

	/**
	 * Returns a synchronized (thread-safe) accumulator backed by the specified
	 * {@code accumulator}. The given {@code accumulator} is used for as
	 * synchronization object.
	 *
	 * @param accumulator the accumulator to be "wrapped" in a synchronized
	 *        accumulator
	 * @param <T> the type of input elements to the accumulate operation
	 * @param <R> the result type of the accumulated operation
	 * @return a synchronized view of the specified accumulator
	 * @throws NullPointerException if the {@code accumulator} is {@code null}
	 */
	static <T, R> Accumulator<T, ?, R>
	sync(final Accumulator<T, ?, R> accumulator) {
		return sync(accumulator, accumulator);
	}

}

