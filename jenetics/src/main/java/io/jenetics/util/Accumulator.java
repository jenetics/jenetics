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

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * This interface lets you accumulate elements of type {@code T} to a result of
 * type {@code R}.  In contrast to a {@link Collector} an {@code Accumulator}
 * can deliver intermediate results while accumulating. An accumulator can be
 * created from any {@link Collector} with the ({@link #of(Collector)}) method.
 * {@snippet lang="java":
 * final Accumulator<Integer, ?, List<Integer>> accu =
 *     Accumulator.of(Collectors.toList());
 *
 * final ISeq<List<Integer>> result = IntStream.range(0, 10).boxed()
 *     .peek(accu)
 *     .map(i -> accu.result())
 *     .collect(ISeq.toISeq());
 *
 * result.forEach(System.out::println);
 * }
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
 * not mergeable. The <em>accumulator</em> is not thread-safe and can't be used
 * for parallel streams when used as {@link Consumer} in the
 * {@link java.util.stream.Stream#peek(Consumer)} or
 * {@link java.util.stream.Stream#forEach(Consumer)} method. Obtaining a
 * synchronized view of the accumulator with the {@link #synced()} method, will
 * solve this problem. If the accumulator is used as {@link Collector}, the
 * usage in parallel streams is safe.
 *
 * @param <T> the type of input elements to the accumulate operation
 * @param <A> the accumulator type
 * @param <R> the result type of the accumulated operation
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.1
 * @since 6.1
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

	/**
	 * Combines {@code this} accumulator with the {@code other} one.
	 *
	 * @param other the other accumulator
	 * @return the combined accumulator
	 * @throws UnsupportedOperationException unless it's overridden by the
	 *         implementation
	 */
	default A combine(final A other) {
		throw new UnsupportedOperationException(
			"No implementation for combining accumulators."
		);
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
	 * Returns a synchronized (thread-safe) accumulator backed by {@code this}
	 * accumulator. The given {@code lock} is used as a synchronization object.
	 *
	 * @param lock the <em>lock</em> used for synchronization
	 * @return a synchronized (thread-safe) accumulator backed by {@code this}
	 * 	       accumulator
	 * @throws NullPointerException if the given {@code lock} is {@code null}
	 */
	default Accumulator<T, ?, R> synced(final Object lock) {
		requireNonNull(lock);

		@SuppressWarnings("unchecked")
		final A self = (A)this;
		return this instanceof SynchronizedAccumulator
			? this
			: new SynchronizedAccumulator<>(self, lock);
	}

	/**
	 * Returns a synchronized (thread-safe) accumulator backed by {@code this}
	 * accumulator. {@code this} accumulator is used as a synchronization object.
	 *
	 * @return a synchronized (thread-safe) accumulator backed by {@code this}
	 * 	       accumulator
	 */
	default Accumulator<T, ?, R> synced() {
		return synced(this);
	}

	/**
	 * Create a new accumulator from the given {@code collector}.
	 *
	 * {@snippet lang="java":
	 * final Accumulator<Integer, ?, ISeq<Integer>> accu =
	 *     Accumulator.of(ISeq.toISeq());
	 * }
	 *
	 * @param collector the collector which is used for accumulation and creation
	 *        the result value.
	 * @param <T> the type of input elements to the reduction operation
	 * @param <R> the result type of the reduction operation
	 * @return a new accumulator which is backed by the given {@code collector}
	 * @throws NullPointerException if the given {@code collector} is {@code null}
	 */
	static <T, R> Accumulator<T, ?, R> of(final Collector<T, ?, R> collector) {
		return new CollectorAccumulator<>(collector);
	}

}

