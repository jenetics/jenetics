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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.jenetics.internal.collection.Empty;
import org.jenetics.internal.util.require;

/**
 * Immutable, ordered, fixed sized sequence.
 *
 * @see MSeq
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.4
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

	@SuppressWarnings("unchecked")
	@Override
	public default ISeq<T> append(final T... values) {
		return append(ISeq.of(values));
	}

	@Override
	public ISeq<T> append(final Iterable<? extends T> values);

	@SuppressWarnings("unchecked")
	@Override
	public default ISeq<T> prepend(final T... values) {
		return prepend(ISeq.of(values));
	}

	@Override
	public ISeq<T> prepend(final Iterable<? extends T> values);

	/**
	 * Return a shallow copy of this sequence. The sequence elements are not
	 * cloned.
	 *
	 * @return a shallow copy of this sequence.
	 */
	@Override
	public MSeq<T> copy();


	/* *************************************************************************
	 *  Some static helper methods.
	 * ************************************************************************/

	/**
	 * Return a sequence whose elements are all the elements of the first
	 * element followed by all the elements of the sequence.
	 *
	 * @since !__version__!
	 *
	 * @param a the first element
	 * @param b the appending sequence
	 * @param <T> the type of the sequence elements
	 * @return the concatenation of the two inputs
	 * @throws NullPointerException if one of the second arguments is
	 *         {@code null}
	 */
	@SuppressWarnings("unchecked")
	public static <T> ISeq<T> concat(
		final T a,
		final ISeq<? extends T> b
	) {
		return ((ISeq<T>)b).prepend(a);
	}

	/**
	 * Return a sequence whose elements are all the elements of the first
	 * sequence followed by all the elements of the vararg array.
	 *
	 * @since !__version__!
	 *
	 * @param a the first sequence
	 * @param b the vararg elements
	 * @param <T> the type of the sequence elements
	 * @return the concatenation of the two inputs
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	@SuppressWarnings("unchecked")
	public static <T> ISeq<T> concat(
		final ISeq<? extends T> a,
		final T... b
	) {
		return ((ISeq<T>)a).append(b);
	}

	/**
	 * Return a sequence whose elements are all the elements of the first
	 * sequence followed by all the elements of the second sequence.
	 *
	 * @since !__version__!
	 *
	 * @param a the first sequence
	 * @param b the second sequence
	 * @param <T> the type of the sequence elements
	 * @return the concatenation of the two input sequences
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	@SuppressWarnings("unchecked")
	public static <T> ISeq<T> concat(
		final ISeq<? extends T> a,
		final ISeq<? extends T> b
	) {
		return ((ISeq<T>)a).append(b);
	}

	/* *************************************************************************
	 *  Some static factory methods.
	 * ************************************************************************/

	/**
	 * Single instance of an empty {@code ISeq}.
	 *
	 * @since 3.3
	 */
	public static final ISeq<?> EMPTY = Empty.ISEQ;

	/**
	 * Return an empty {@code ISeq}.
	 *
	 * @since 3.3
	 *
	 * @param <T> the element type of the returned {@code ISeq}.
	 * @return an empty {@code ISeq}.
	 */
	public static <T> ISeq<T> empty() {
		return Empty.iseq();
	}

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
		return values.length == 0
			? empty()
			: MSeq.of(values).toISeq();
	}

	/**
	 * Create a new {@code ISeq} from the given values.
	 *
	 * @param <T> the element type
	 * @param values the array values.
	 * @return a new {@code ISeq} with the given values.
	 * @throws NullPointerException if the {@code values} array is {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> ISeq<T> of(final Iterable<? extends T> values) {
		requireNonNull(values);

		return values instanceof ISeq<?>
			? (ISeq<T>)values
			: values instanceof MSeq<?>
				? ((MSeq<T>)values).toISeq()
				: MSeq.of(values).toISeq();
	}

//	/**
//	 * Create a new {@code ISeq} instance from the remaining elements of the
//	 * given iterator.
//	 *
//	 * @since 3.3
//	 *
//	 * @param <T> the element type.
//	 * @return a new {@code ISeq} with the given remaining values.
//	 * @throws NullPointerException if the {@code values} object is
//	 *        {@code null}.
//	 */
//	public static <T> ISeq<T> of(final Iterator<? extends T> values) {
//		final MSeq<T> seq = MSeq.of(values);
//		return seq.isEmpty() ? empty() : seq.toISeq();
//	}

	/**
	 * Creates a new sequence, which is filled with objects created be the given
	 * {@code supplier}.
	 *
	 * @since 3.2
	 *
	 * @param <T> the element type of the sequence
	 * @param supplier the {@code Supplier} which creates the elements, the
	 *        returned sequence is filled with
	 * @param length the length of the returned sequence
	 * @return a new sequence filled with elements given by the {@code supplier}
	 * @throws NegativeArraySizeException if the given {@code length} is
	 *         negative
	 * @throws NullPointerException if the given {@code supplier} is
	 *         {@code null}
	 */
	public static <T> ISeq<T> of(
		final Supplier<? extends T> supplier,
		final int length
	) {
		requireNonNull(supplier);
		require.nonNegative(length);

		return length == 0
			? empty()
			: MSeq.<T>ofLength(length).fill(supplier).toISeq();
	}

}
