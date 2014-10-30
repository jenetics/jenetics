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
package org.jenetics.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Immutable, ordered, fixed sized sequence.
 *
 * @see MSeq
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.0 &mdash; <em>$Date: 2014-10-07 $</em>
 */
public interface ISeq<T>
	extends
		Seq<T>,
		Copyable<MSeq<T>>
{

	@Override
	public ISeq<T> subSeq(final int start, final int end);

	@Override
	public ISeq<T> subSeq(final int start);

	@Override
	public <B> ISeq<B> map(final Function<? super T, ? extends B> mapper);

	/**
	 * Return a shallow copy of this sequence. The sequence elements are not
	 * cloned.
	 *
	 * @return a shallow copy of this sequence.
	 */
	@Override
	public MSeq<T> copy();


	/* *************************************************************************
	 *  Some static factory methods.
	 * ************************************************************************/

	/**
	 * Returns a {@code Collector} that accumulates the input elements into a
	 * new {@code ISeq}.
	 *
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} which collects all the input elements into an
	 *         {@code ISeq}, in encounter order
	 */
	public static <T> Collector<T, ?, ISeq<T>> toISeq() {
		return Collector.of(
			(Supplier<List<T>>)ArrayList::new,
			List::add,
			(left, right) -> { left.addAll(right); return left; },
			ISeq::of
		);
	}

	/**
	 * Create a new {@code ISeq} from the given values.
	 *
	 * @param <T> the element type
	 * @param values the array values.
	 * @return a new {@code ISeq} with the given values.
	 * @throws NullPointerException if the {@code values} array is {@code null}.
	 */
	@SafeVarargs
	public static <T> ISeq<T> of(final T... values) {
		return MSeq.of(values).toISeq();
	}

	/**
	 * Create a new {@code ISeq} from the given values.
	 *
	 * @param <T> the element type
	 * @param values the array values.
	 * @return a new {@code ISeq} with the given values.
	 * @throws NullPointerException if the {@code values} array is {@code null}.
	 */
	public static <T> ISeq<T> of(final Iterable<? extends T> values) {
		return MSeq.of(values).toISeq();
	}

	public static <T> ISeq<T> of(Supplier<? extends T> supplier, final int length) {
		return MSeq.<T>ofLength(length).fill(supplier).toISeq();
	}

}
